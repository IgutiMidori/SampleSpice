package jp.ac.ttc.webapp.bean;

import java.io.Serializable;

public class OtpBean implements Serializable{
    private int otpId;
    private int otp;
    private String createdAt;

    public OtpBean() {}

    public OtpBean(int otpId, int otp, String createdAt) {
        this.otpId = otpId;
        this.otp = otp;
        this.createdAt = createdAt;
    }
    public int getOtpId() {
        return otpId;
    }
    public int getOtp() {
        return otp;
    }
    public String getCreatedAt() {
        return createdAt;
    }
}