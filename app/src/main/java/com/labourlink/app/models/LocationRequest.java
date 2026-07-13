package com.labourlink.app.models;

public class LocationRequest {

    private Double latitude;
    private Double longitude;

    public LocationRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}