package com.labourlink.app.models;

import java.util.List;

public class OwnerRequestsResponse {

    private int totalRequests;

    private int upcomingCount;

    private int pastCount;

    private List<OwnerRequest> upcoming;

    private List<OwnerRequest> past;

    public OwnerRequestsResponse() {
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getUpcomingCount() {
        return upcomingCount;
    }

    public void setUpcomingCount(int upcomingCount) {
        this.upcomingCount = upcomingCount;
    }

    public int getPastCount() {
        return pastCount;
    }

    public void setPastCount(int pastCount) {
        this.pastCount = pastCount;
    }

    public List<OwnerRequest> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(List<OwnerRequest> upcoming) {
        this.upcoming = upcoming;
    }

    public List<OwnerRequest> getPast() {
        return past;
    }

    public void setPast(List<OwnerRequest> past) {
        this.past = past;
    }
}