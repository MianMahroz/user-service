package com.caam.user.dto;


import com.caam.commons.base.domain.BaseDomain;

public class LoginDto extends BaseDomain {

    private String loginString;

    private String password;

    public LoginDto() {

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
