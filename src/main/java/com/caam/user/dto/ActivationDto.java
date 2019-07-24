package com.caam.user.dto;

public class ActivationDto {

	private String activationToken;
	private int activationOtp;

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

	public ActivationDto() {

	}

}
