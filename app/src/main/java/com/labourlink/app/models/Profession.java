package com.labourlink.app.models;

public class Profession {

    private Long id;
    private String name;
    private String description;
    private Double dailyWage;

    public Profession() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(Double dailyWage) {
        this.dailyWage = dailyWage;


    }
    @Override
    public String toString() {
        // This is very important for Spinner
        return name;
    }

}