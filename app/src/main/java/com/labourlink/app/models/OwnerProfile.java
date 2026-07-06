package com.labourlink.app.models;

public class OwnerProfile {

    private String name;
    private String phoneNumber;
    private String joinedDate;

    private Integer totalRequests;
    private Integer upcomingRequests;
    private Integer pastRequests;

    public OwnerProfile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getUpcomingRequests() {
        return upcomingRequests;
    }

    public void setUpcomingRequests(Integer upcomingRequests) {
        this.upcomingRequests = upcomingRequests;
    }

    public Integer getPastRequests() {
        return pastRequests;
    }

    public void setPastRequests(Integer pastRequests) {
        this.pastRequests = pastRequests;
    }
}