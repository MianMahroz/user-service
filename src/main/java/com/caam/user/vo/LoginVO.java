package com.caam.user.vo;

public class LoginVO {
    private String loginString;

    private String password;

    public LoginVO() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginString() {
        return loginString;
    }

    public void setLoginString(String loginString) {
        this.loginString = loginString;
    }
}
