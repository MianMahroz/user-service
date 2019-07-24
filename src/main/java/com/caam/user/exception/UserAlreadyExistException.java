package com.caam.user.exception;

import com.caam.commons.exceptionhandler.BaseException;

public class UserAlreadyExistException extends BaseException {
    public UserAlreadyExistException(int code) {
        super(code);
    }
}
