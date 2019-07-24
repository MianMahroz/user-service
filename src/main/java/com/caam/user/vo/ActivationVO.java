package com.caam.user.vo;

public class ActivationVO {
    private String activationToken;
    private int activationOtp;


    public ActivationVO() {
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public int getActivationOtp() {
        return activationOtp;
    }

    public void setActivationOtp(int activationOtp) {
        this.activationOtp = activationOtp;
    }
}
