package com.labourlink.app.models;

public class VerifyOtpRequest {

    private String name;
    private String phoneNumber;
    private String password;
    private String otp;

    public VerifyOtpRequest(String name,
                            String phoneNumber,
                            String password,
                            String otp) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.otp = otp;
    }
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getOtp() {
        return otp;
    }
}