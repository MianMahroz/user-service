package com.caam.user.vo;

public class ResetPasswordVO {
    private String loginString;
    private String password;


	public ResetPasswordVO() {
    }
	
	public String getLoginString() {
		return loginString;
	}


	public void setLoginString(String loginString) {
		this.loginString = loginString;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

}