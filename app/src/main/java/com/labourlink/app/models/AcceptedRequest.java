package com.labourlink.app.models;

public class AcceptedRequest {

    private Long requestId;

    private String title;

    private String ownerName;

    private String ownerPhone;

    private String profession;

    private String description;

    private String address;

    private Double distance;

    private String workDate;

    private String startTime;

    private String endTime;

    private String status;

    private Double latitude;

    private Double longitude;
    private Integer rating;

    public Integer getRating() {
        return rating;
    }
    public Long getRequestId() {
        return requestId;
    }

    public String getTitle() {
        return title;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

}