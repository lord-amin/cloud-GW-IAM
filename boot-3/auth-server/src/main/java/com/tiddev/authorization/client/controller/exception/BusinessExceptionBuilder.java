package com.tiddev.authorization.client.controller.exception;

import java.util.HashMap;
import java.util.Map;

public final class BusinessExceptionBuilder {
    private Map<String, Object> metadata;
    private ExceptionCode exceptionCode;
    private Throwable cause;

    private BusinessExceptionBuilder() {
    }

    public static BusinessExceptionBuilder newInstance() {
        return new BusinessExceptionBuilder();
    }

    public BusinessExceptionBuilder withMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public BusinessExceptionBuilder addMetadata(String key, Object value) {
        if (this.metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
        return this;
    }

    public BusinessExceptionBuilder addMetadata(MetaDataEntry... entries) {
        if (this.metadata == null) {
            metadata = new HashMap<>();
        }
        for (Map.Entry<String, Object> entry : entries) {
            metadata.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BusinessExceptionBuilder withExceptionCode(GeneralCodes exceptionCode) {
        this.exceptionCode = exceptionCode;
        return this;
    }

    public BusinessExceptionBuilder withCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public BusinessException build() {
        BusinessException businessException = new BusinessException(exceptionCode, cause);
        businessException.setMetadata(metadata);
        return businessException;
    }

    public static class MetaDataEntry implements Map.Entry<String, Object> {
        private String key;
        private Object value;

        public MetaDataEntry() {
        }

        public MetaDataEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(Object value) {
            this.value = value;
            return value;
        }
    }
}