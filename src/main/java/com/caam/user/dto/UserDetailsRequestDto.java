package com.caam.user.dto;
import java.io.Serializable;
import java.util.List;
public class UserDetailsRequestDto implements Serializable {

    List list;

    public UserDetailsRequestDto() {

    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
