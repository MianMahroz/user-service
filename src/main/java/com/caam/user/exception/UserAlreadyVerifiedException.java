package com.caam.user.exception;
import com.caam.commons.exceptionhandler.BaseException;

public class UserAlreadyVerifiedException extends BaseException {
    public UserAlreadyVerifiedException(int code) {
        super(code);
    }
}
