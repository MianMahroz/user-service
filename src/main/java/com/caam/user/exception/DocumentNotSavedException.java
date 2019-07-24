package com.caam.user.exception;

import com.caam.commons.exceptionhandler.BaseException;

public class DocumentNotSavedException extends BaseException {
    public DocumentNotSavedException(int code) {
        super(code);
    }
}
