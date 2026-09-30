package com.floodpath.service;

import com.floodpath.config.DbConfig;
import com.floodpath.model.FloodReport;
import com.floodpath.model.Road;
import com.floodpath.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class FloodService {
    private static final FloodService INSTANCE = new FloodService();
    private static final int REPORT_LIFETIME_HOURS = 2;
    private final Map<Integer, Road> roads = new ConcurrentHashMap<>();
    private final List<FloodReport> reports = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger reportId = new AtomicInteger(1000);

    private FloodService(){ seedDemoRoads(); loadFromDb(); }
    public static FloodService getInstance(){return INSTANCE;}

    private void seedDemoRoads(){
        addRoad(new Road(1,"Lake View Road","A","B",1.20,"NORMAL",0));
        addRoad(new Road(2,"Market Road","B","C",1.00,"NORMAL",0));
        addRoad(new Road(3,"Canal Road","A","D",1.40,"NORMAL",0));
        addRoad(new Road(4,"Ring Road","D","E",1.10,"NORMAL",0));
        addRoad(new Road(5,"Station Road","E","C",1.30,"NORMAL",0));
        addRoad(new Road(6,"Bridge Road","B","E",0.90,"NORMAL",0));
        addRoad(new Road(7,"Temple Road","C","F",1.00,"NORMAL",0));
        addRoad(new Road(8,"School Road","E","F",0.80,"NORMAL",0));
    }

    private void addRoad(Road r){roads.put(r.getId(),r);}
    private Connection connection() throws SQLException{return DriverManager.getConnection(DbConfig.URL,DbConfig.USER,DbConfig.PASSWORD);}

    private void loadFromDb(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); ensureSchema();
            try(Connection c=connection(); PreparedStatement ps=c.prepareStatement("SELECT id,road_name,start_node,end_node,distance_km,status,severity,simulated_water_cm,simulated_rain_intensity FROM roads"); ResultSet rs=ps.executeQuery()){
                while(rs.next()) addRoad(new Road(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getDouble(5),rs.getString(6),rs.getInt(7),rs.getInt(8),rs.getInt(9)));
            }
            loadReports();
            refreshExpiredReports();
        } catch(Exception ignored) {}
    }

    private void ensureSchema(){
        try(Connection c=connection(); Statement s=c.createStatement()){
            addColumn(c,"roads","simulated_water_cm","INT NOT NULL DEFAULT 0");
            addColumn(c,"roads","simulated_rain_intensity","INT NOT NULL DEFAULT 0");
            addColumn(c,"flood_reports","starts_at","DATETIME NULL");
            addColumn(c,"flood_reports","ends_at","DATETIME NULL");
            addColumn(c,"flood_reports","status","VARCHAR(20) NOT NULL DEFAULT 'PENDING'");
            try{s.executeUpdate("UPDATE flood_reports SET starts_at=created_at, ends_at=DATE_ADD(created_at, INTERVAL 2 HOUR) WHERE starts_at IS NULL OR ends_at IS NULL");}catch(Exception ignored){}
        }catch(Exception ignored){}
    }

    private void addColumn(Connection c, String table, String column, String definition) {
        try {
            DatabaseMetaData md = c.getMetaData();
            try (ResultSet rs = md.getColumns(null, null, table, column)) {
                if (!rs.next()) {
                    try (Statement s = c.createStatement()) {
                        s.executeUpdate(
                            "ALTER TABLE " + table +
                            " ADD COLUMN " + column + " " + definition
                        );
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void loadReports() throws SQLException {
        reports.clear(); int max=1000;
        String sql="SELECT fr.id,fr.road_id,r.road_name,fr.water_level_cm,fr.severity,fr.road_condition,fr.description,fr.status,COALESCE(fr.user_id,0),COALESCE(u.name,'Unknown'),fr.image_url,fr.created_at,fr.starts_at,fr.ends_at FROM flood_reports fr JOIN roads r ON r.id=fr.road_id LEFT JOIN users u ON u.id=fr.user_id ORDER BY fr.id";
        try(Connection c=connection(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                Timestamp created=rs.getTimestamp("created_at"), starts=rs.getTimestamp("starts_at"), ends=rs.getTimestamp("ends_at");
                FloodReport r=new FloodReport(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getInt(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getInt(9),rs.getString(10),rs.getString(11),
                        created==null?LocalDateTime.now():created.toLocalDateTime(), starts==null?(created==null?LocalDateTime.now():created.toLocalDateTime()):starts.toLocalDateTime(), ends==null?(created==null?LocalDateTime.now().plusHours(REPORT_LIFETIME_HOURS):created.toLocalDateTime().plusHours(REPORT_LIFETIME_HOURS)):ends.toLocalDateTime());
                reports.add(r); max=Math.max(max,r.getId());
            }
        }
        reportId.set(max);
    }

    public Collection<Road> getRoads(){refreshExpiredReports();return new ArrayList<>(roads.values());}
    public List<FloodReport> getReports(){refreshExpiredReports();synchronized(reports){return new ArrayList<>(reports);}}
    public List<FloodReport> getReportsByUser(int userId){refreshExpiredReports();List<FloodReport> out=new ArrayList<>();synchronized(reports){for(FloodReport r:reports)if(r.getUserId()==userId)out.add(r);}Collections.reverse(out);return out;}
    public FloodReport getReport(int id){refreshExpiredReports();synchronized(reports){for(FloodReport r:reports)if(r.getId()==id)return r;}return null;}

    public FloodReport addReport(User user,int roadId,int waterLevel,int severity,String condition,String description,String imageFile){
        Road road=roads.get(roadId); if(road==null)throw new IllegalArgumentException("Unknown road");
        LocalDateTime start=LocalDateTime.now(), end=start.plusHours(REPORT_LIFETIME_HOURS);
        FloodReport report=new FloodReport(0,roadId,road.getName(),waterLevel,severity,condition,description,"PENDING",user.getId(),user.getName(),imageFile,start,start,end);
        int id=persistReport(report,user);
        report=new FloodReport(id,roadId,road.getName(),waterLevel,severity,condition,description,"PENDING",user.getId(),user.getName(),imageFile,start,start,end);
        reports.add(report); reportId.set(Math.max(reportId.get(),id)); recomputeRoad(roadId); return report;
    }

    public FloodReport setReportStatus(int id,String status){
        if(!Set.of("VERIFIED","REJECTED","PENDING","CLOSED").contains(status))throw new IllegalArgumentException("Bad status");
        FloodReport r=getReport(id); if(r==null)return null; r.setStatus(status); persistReportStatus(r); recomputeRoad(r.getRoadId()); return r;
    }

    public void resetRoads(){for(Road r:roads.values()){r.setWaterLevelCm(0);r.setRainIntensity(0);recomputeRoad(r.getId());}}

    public void simulate(int roadId,int waterLevel,int rainIntensity){
        Road r=roads.get(roadId); if(r==null)throw new IllegalArgumentException("Unknown road");
        if(waterLevel<0||waterLevel>200)throw new IllegalArgumentException("Water depth must be 0-200 cm");
        if(rainIntensity<0||rainIntensity>100)throw new IllegalArgumentException("Rain intensity must be 0-100%");
        r.setWaterLevelCm(waterLevel); r.setRainIntensity(rainIntensity); recomputeRoad(roadId);
    }

    public int severityFromWater(int water){return water<=10?0:water<=25?1:water<=50?2:water<=79?3:4;}
    public String statusFromSeverity(int severity){return switch(severity){case 0->"NORMAL";case 1->"MINOR";case 2->"MODERATE";case 3->"SEVERE";default->"CLOSED";};}

    private void recomputeRoad(int roadId){
        Road road=roads.get(roadId); if(road==null)return; int maxSeverity=severityFromWater(road.getWaterLevelCm());
        synchronized(reports){for(FloodReport r:reports)if(r.getRoadId()==roadId&&r.isLive())maxSeverity=Math.max(maxSeverity,r.getSeverity());}
        road.setSeverity(maxSeverity); road.setStatus(statusFromSeverity(maxSeverity)); persistRoad(road);
    }

    private void refreshExpiredReports() {
        LocalDateTime now = LocalDateTime.now();
        List<Integer> roadsToRefresh = new ArrayList<>();

        synchronized (reports) {
            for (FloodReport r : reports) {
                if (("PENDING".equals(r.getStatus()) ||
                     "VERIFIED".equals(r.getStatus()))
                        && r.getEndsAt() != null) {
                    try {
                        LocalDateTime endTime =
                            LocalDateTime.parse(
                                r.getEndsAt(),
                                java.time.format.DateTimeFormatter.ofPattern(
                                    "dd MMM yyyy, HH:mm"
                                )
                            );

                        if (!now.isBefore(endTime)) {
                            r.setStatus("EXPIRED");
                            persistReportStatus(r);
                            roadsToRefresh.add(r.getRoadId());
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        for (Integer id : new HashSet<>(roadsToRefresh)) {
            recomputeRoadWithoutExpiry(id);
        }
    }

    private void recomputeRoadWithoutExpiry(int roadId){
        Road road=roads.get(roadId);if(road==null)return;int max=severityFromWater(road.getWaterLevelCm());
        synchronized(reports){for(FloodReport r:reports)if(r.getRoadId()==roadId&&r.isLive())max=Math.max(max,r.getSeverity());}
        road.setSeverity(max);road.setStatus(statusFromSeverity(max));persistRoad(road);
    }

    private void persistRoad(Road r){try(Connection c=connection();PreparedStatement ps=c.prepareStatement("UPDATE roads SET status=?,severity=?,simulated_water_cm=?,simulated_rain_intensity=? WHERE id=?")){ps.setString(1,r.getStatus());ps.setInt(2,r.getSeverity());ps.setInt(3,r.getWaterLevelCm());ps.setInt(4,r.getRainIntensity());ps.setInt(5,r.getId());ps.executeUpdate();}catch(Exception ignored){}}

    private int persistReport(FloodReport r, User user) {
        try (
            Connection c = connection();
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO flood_reports(" +
                "user_id,road_id,water_level_cm,severity,road_condition," +
                "description,image_url,status,starts_at,ends_at" +
                ") VALUES(?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            if (user.isPersisted()) {
                ps.setInt(1, user.getId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setInt(2, r.getRoadId());
            ps.setInt(3, r.getWaterLevelCm());
            ps.setInt(4, r.getSeverity());
            ps.setString(5, r.getRoadCondition());
            ps.setString(6, r.getDescription());
            ps.setString(7, r.getImageFile());
            ps.setString(8, r.getStatus());

            LocalDateTime startTime =
                LocalDateTime.parse(
                    r.getStartsAt(),
                    java.time.format.DateTimeFormatter.ofPattern(
                        "dd MMM yyyy, HH:mm"
                    )
                );

            LocalDateTime endTime =
                LocalDateTime.parse(
                    r.getEndsAt(),
                    java.time.format.DateTimeFormatter.ofPattern(
                        "dd MMM yyyy, HH:mm"
                    )
                );

            ps.setTimestamp(9, Timestamp.valueOf(startTime));
            ps.setTimestamp(10, Timestamp.valueOf(endTime));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reportId.incrementAndGet();
    }

    private void persistReportStatus(FloodReport r){try(Connection c=connection();PreparedStatement ps=c.prepareStatement("UPDATE flood_reports SET status=? WHERE id=?")){ps.setString(1,r.getStatus());ps.setInt(2,r.getId());ps.executeUpdate();}catch(Exception ignored){}}

    public List<Road> calculateRoute(String start,String destination){
        refreshExpiredReports(); Map<String,List<Road>> graph=new HashMap<>();
        for(Road r:roads.values()){graph.computeIfAbsent(r.getStart(),k->new ArrayList<>()).add(r);graph.computeIfAbsent(r.getEnd(),k->new ArrayList<>()).add(new Road(r.getId(),r.getName(),r.getEnd(),r.getStart(),r.getDistanceKm(),r.getStatus(),r.getSeverity(),r.getWaterLevelCm(),r.getRainIntensity()));}
        if(!graph.containsKey(start)||!graph.containsKey(destination))return List.of(); Map<String,Double> dist=new HashMap<>();Map<String,Road> prev=new HashMap<>();PriorityQueue<String> pq=new PriorityQueue<>(Comparator.comparingDouble(n->dist.getOrDefault(n,Double.POSITIVE_INFINITY)));dist.put(start,0.0);pq.add(start);
        while(!pq.isEmpty()){String u=pq.poll();if(u.equals(destination))break;for(Road e:graph.getOrDefault(u,List.of())){if("CLOSED".equals(e.getStatus()))continue;double cost=e.getDistanceKm()+e.getSeverity()*2.5,nd=dist.get(u)+cost;if(nd<dist.getOrDefault(e.getEnd(),Double.POSITIVE_INFINITY)){dist.put(e.getEnd(),nd);prev.put(e.getEnd(),e);pq.remove(e.getEnd());pq.add(e.getEnd());}}}
        if(!dist.containsKey(destination))return List.of();List<Road> path=new ArrayList<>();String cur=destination;while(!cur.equals(start)){Road e=prev.get(cur);if(e==null)return List.of();path.add(e);cur=e.getStart();}Collections.reverse(path);return path;
    }
}