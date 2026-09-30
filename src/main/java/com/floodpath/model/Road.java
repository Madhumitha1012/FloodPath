package com.floodpath.model;

public class Road {
    private final int id;
    private final String name;
    private final String start;
    private final String end;
    private final double distanceKm;
    private String status;
    private int severity;
    private int waterLevelCm;
    private int rainIntensity;

    public Road(int id, String name, String start, String end, double distanceKm, String status, int severity) {
        this(id, name, start, end, distanceKm, status, severity, 0, 0);
    }

    public Road(int id, String name, String start, String end, double distanceKm, String status, int severity, int waterLevelCm, int rainIntensity) {
        this.id = id; this.name = name; this.start = start; this.end = end; this.distanceKm = distanceKm;
        this.status = status; this.severity = severity; this.waterLevelCm = waterLevelCm; this.rainIntensity = rainIntensity;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public double getDistanceKm() { return distanceKm; }
    public String getStatus() { return status; }
    public int getSeverity() { return severity; }
    public int getWaterLevelCm() { return waterLevelCm; }
    public int getRainIntensity() { return rainIntensity; }
    public void setStatus(String status) { this.status = status; }
    public void setSeverity(int severity) { this.severity = severity; }
    public void setWaterLevelCm(int waterLevelCm) { this.waterLevelCm = waterLevelCm; }
    public void setRainIntensity(int rainIntensity) { this.rainIntensity = rainIntensity; }
}
