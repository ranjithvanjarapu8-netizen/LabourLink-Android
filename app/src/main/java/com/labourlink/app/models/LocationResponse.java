package com.labourlink.app.models;

import com.google.gson.annotations.SerializedName;

public class LocationResponse {

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lon")
    private String lon;

    public String getDisplayName() {
        return displayName;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}