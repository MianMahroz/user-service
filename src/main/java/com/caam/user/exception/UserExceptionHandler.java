package com.caam.user.exception;

import com.caam.commons.response.utills.ResponseUtill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import static com.caam.commons.constants.CaamResponseCodes.FAILURE_CODE;

@ControllerAdvice
@RestController
public class UserExceptionHandler {


    ResponseUtill responseUtill = new ResponseUtill();

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllExceptions(Exception ex) {
        return responseUtill.getApiResponse(FAILURE_CODE, ex.getMessage(), null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<?> caamUserNotFoundExceptionHandle(UserNotFoundException ex) {
        return responseUtill.getApiResponse(ex.getCode(), null, null);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<?> caamUserAlreadyExceptionHandle(UserAlreadyExistException ex) {
        return responseUtill.getApiResponse(ex.getCode(), null, null);
    }
    
    @ExceptionHandler(UserBadRequestException.class)
    public final ResponseEntity<?> caamUserBadRequestExceptionHandle(UserBadRequestException ex) {
        return responseUtill.getApiResponse(ex.getCode(), null, null);
    }

    @ExceptionHandler(DocumentNotSavedException.class)
    public final ResponseEntity<?> caamDocumentNotSavedExceptionHandle(DocumentNotSavedException ex) {
        return responseUtill.getApiResponse(ex.getCode(), null, null);
    }
}
