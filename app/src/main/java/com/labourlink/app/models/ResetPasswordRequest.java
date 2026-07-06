package com.labourlink.app.models;

public class ResetPasswordRequest {

    private String phoneNumber;
    private String newPassword;

    public ResetPasswordRequest(String phoneNumber,
                                String newPassword) {
        this.phoneNumber = phoneNumber;
        this.newPassword = newPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNewPassword() {
        return newPassword;
    }
}