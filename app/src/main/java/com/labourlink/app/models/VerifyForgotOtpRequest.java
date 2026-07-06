package com.labourlink.app.models;


public class VerifyForgotOtpRequest {

    private String phoneNumber;
    private String otp;

    public VerifyForgotOtpRequest(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOtp() {
        return otp;
    }
}
