package com.caam.user.exception;

import com.caam.commons.exceptionhandler.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(int code) {
        super(code);
    }
}
