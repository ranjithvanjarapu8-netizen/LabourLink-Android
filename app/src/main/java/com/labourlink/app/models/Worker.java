package com.labourlink.app.models;

import java.util.List;

public class Worker {

    private Long id;
    private Integer experience;
    private Double latitude;
    private Double longitude;
    private String city;
    private String district;
    private String state;
    private Boolean available;
    private Double rating;
    private Integer totalRatings;
    private Integer totalJobs;
    private String languages;
    private String aadhaarNumber;
    private String profilePhoto;
    private String description;

    // If your backend returns professions
    private List<String> professions;
    private String name;
    private String phoneNumber;
    private String createdAt;

    public Long getId() {
        return id;
    }

    public Integer getExperience() {
        return experience;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public Boolean getAvailable() {
        return available;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public Integer getTotalJobs() {
        return totalJobs;
    }

    public String getLanguages() {
        return languages;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getProfessions() {
        return professions;
    }
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}