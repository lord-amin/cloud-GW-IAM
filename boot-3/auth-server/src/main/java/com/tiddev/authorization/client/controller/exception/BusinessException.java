package com.tiddev.authorization.client.controller.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {

    public BusinessException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public BusinessException(ExceptionCode exceptionCode, HttpStatus httpStatus) {
        super(exceptionCode);
    }

    public BusinessException(ExceptionCode exceptionCode, String message) {
        super(exceptionCode, message);
    }

    public BusinessException(ExceptionCode exceptionCode, HttpStatus httpStatus, String message) {
        super(exceptionCode, message);
    }

    public BusinessException(ExceptionCode exceptionCode, String message, Throwable cause) {
        super(exceptionCode, message, cause);
    }

    public BusinessException(ExceptionCode exceptionCode, HttpStatus httpStatus, String message, Throwable cause) {
        super(exceptionCode, message, cause);
    }

    public BusinessException(ExceptionCode exceptionCode, Throwable cause) {
        super(exceptionCode, cause);
    }

    public BusinessException(ExceptionCode exceptionCode, HttpStatus httpStatus, Throwable cause) {
        super(exceptionCode, cause);
    }

    public BusinessException(ExceptionCode exceptionCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(exceptionCode, message, cause, enableSuppression, writableStackTrace);
    }

    public BusinessException(ExceptionCode exceptionCode, HttpStatus httpStatus, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(exceptionCode, message, cause, enableSuppression, writableStackTrace);
    }



}