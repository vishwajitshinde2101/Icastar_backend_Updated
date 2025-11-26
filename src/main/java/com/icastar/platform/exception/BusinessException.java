package com.icastar.platform.exception;

import com.icastar.platform.constants.ApplicationConstants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Object details;

    public BusinessException(String message) {
        super(message);
        this.errorCode = ApplicationConstants.ErrorMessages.OPERATION_FAILED;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.details = null;
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.details = null;
    }

    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = null;
    }

    public BusinessException(String message, String errorCode, HttpStatus httpStatus, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = details;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ApplicationConstants.ErrorMessages.OPERATION_FAILED;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.details = null;
    }
}
