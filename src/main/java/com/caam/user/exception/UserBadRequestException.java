package com.caam.user.exception;

import com.caam.commons.exceptionhandler.BaseException;

public class UserBadRequestException extends BaseException {
    public UserBadRequestException(int code) {
        super(code);
    }
}
