package com.tiddev.authorization.client.controller.exception;

import java.util.Map;

public abstract class BaseException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    private Map<String, Object> metadata;

    public BaseException(ExceptionCode exceptionCode) {
        super(String.format("%s.%s", exceptionCode.getClass().getSimpleName(), exceptionCode.getText()));
        this.exceptionCode = exceptionCode;
    }

    public BaseException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;

    }

    public BaseException(ExceptionCode exceptionCode, String message, Throwable cause) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }

    public BaseException(ExceptionCode exceptionCode, Throwable cause) {
        super(String.format("%s.%s", exceptionCode.getClass().getSimpleName(), exceptionCode.getText()), cause);
        this.exceptionCode = exceptionCode;
    }

    public BaseException(ExceptionCode exceptionCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionCode = exceptionCode;
    }


    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilderImpl(this);
        builder.append("superClass", super.toString());
        builder.append("exceptionCode", exceptionCode);
        builder.append("exceptionCodeValue", exceptionCode == null ? null : exceptionCode.getPrefixValue());
        builder.append("metadata", metadata);
        return builder.toString();
    }
}