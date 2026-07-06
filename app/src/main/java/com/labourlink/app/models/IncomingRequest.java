package com.labourlink.app.models;

public class IncomingRequest {

    private Long requestId;

    private String title;

    private String ownerName;

    private String profession;

    private String description;

    private String address;

    private Double distance;

    private String workDate;

    private String startTime;

    private String endTime;

    private String status;

    public Long getRequestId() {
        return requestId;
    }

    public String getTitle() {
        return title;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getProfession() {
        return profession;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public Double getDistance() {
        return distance;
    }

    public String getWorkDate() {
        return workDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }
}