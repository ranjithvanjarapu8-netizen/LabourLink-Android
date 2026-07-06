package com.labourlink.app.models;

public class LoginRequest {

    private String phoneNumber;
    private String password;

    public LoginRequest(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

}
