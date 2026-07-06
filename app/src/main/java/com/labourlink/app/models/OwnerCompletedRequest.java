package com.labourlink.app.models;

public class OwnerCompletedRequest {

    private Long requestId;

    private String title;

    private String workerName;

    private String profession;

    private String description;

    private String address;

    private String workDate;

    private String startTime;

    private String endTime;

    private String status;

    private Integer rating;

    public Long getRequestId() {
        return requestId;
    }

    public String getTitle() {
        return title;
    }

    public String getWorkerName() {
        return workerName;
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

    public Integer getRating() {
        return rating;
    }

}