package com.floodpath.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FloodReport {


    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");


    private final int id, roadId, waterLevelCm, severity, userId;


    private final String roadName, roadCondition, description, userName, imageFile;


    private final LocalDateTime createdAt, startsAt, endsAt;


    private volatile String status;


    public FloodReport(int id, int roadId, String roadName, int waterLevelCm, int severity, String roadCondition,
                       String description, String status, int userId, String userName, String imageFile,
                       LocalDateTime createdAt, LocalDateTime startsAt, LocalDateTime endsAt) {


        this.id=id; this.roadId=roadId; this.roadName=roadName; this.waterLevelCm=waterLevelCm; this.severity=severity;


        this.roadCondition=roadCondition; this.description=description; this.status=status; this.userId=userId;


        this.userName=userName; this.imageFile=imageFile; this.createdAt=createdAt; this.startsAt=startsAt; this.endsAt=endsAt;
    }


    public int getId(){return id;}


    public int getRoadId(){return roadId;}


    public String getRoadName(){return roadName;}


    public int getWaterLevelCm(){return waterLevelCm;}


    public int getSeverity(){return severity;}


    public String getRoadCondition(){return roadCondition;}


    public String getDescription(){return description;}


    public int getUserId(){return userId;}


    public String getUserName(){return userName;}


    public String getImageFile(){return imageFile;}


    public boolean isHasImage(){return imageFile!=null && !imageFile.isEmpty();}


    public String getCreatedAt(){return createdAt==null?"—":createdAt.format(FMT);}


    public String getStartsAt(){return startsAt==null?"—":startsAt.format(FMT);}


    public String getEndsAt(){return endsAt==null?"—":endsAt.format(FMT);}


    public String getStatus(){return status;}


    public void setStatus(String status){this.status=status;}


    public boolean isLive(){


        return startsAt!=null && endsAt!=null && !LocalDateTime.now().isBefore(startsAt) && LocalDateTime.now().isBefore(endsAt)


                && !"REJECTED".equals(status) && !"CLOSED".equals(status) && !"EXPIRED".equals(status);
    }
}