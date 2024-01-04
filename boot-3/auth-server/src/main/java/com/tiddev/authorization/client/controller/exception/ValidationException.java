package com.tiddev.authorization.client.controller.exception;

public class ValidationException extends BaseException {

    public ValidationException() {
        super(GeneralCodes.VALIDATION_ERROR);
    }

    public ValidationException(String message) {
        super(GeneralCodes.VALIDATION_ERROR, message);
    }

    public ValidationException(String message, Throwable cause) {
        super(GeneralCodes.VALIDATION_ERROR, message, cause);
    }

    public ValidationException(Throwable cause) {
        super(GeneralCodes.VALIDATION_ERROR, cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(GeneralCodes.VALIDATION_ERROR, message, cause, enableSuppression, writableStackTrace);
    }
}