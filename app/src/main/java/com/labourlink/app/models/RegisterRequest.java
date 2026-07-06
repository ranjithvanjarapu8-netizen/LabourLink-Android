package com.labourlink.app.models;

public class RegisterRequest {

    private String name;
    private String phoneNumber;
    private String password;

    public RegisterRequest(String name, String phoneNumber, String password) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}