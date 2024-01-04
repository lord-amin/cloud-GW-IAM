package com.tiddev.authorization.client.controller.exception;

import java.io.Serializable;
import java.util.Map;

public class ExceptionDetail implements Serializable {
    private String key;
    private Map<String, String> description;
    private Map<String, Object> metadata;

    public ExceptionDetail() {
    }

    public ExceptionDetail(String key, Map<String, String> description, Map<String, Object> metadata) {
        this.key = key;
        this.description = description;
        this.metadata = metadata;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getDescription() {
        return this.description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilderImpl(this);
        builder.append("superClass", super.toString());
        builder.append("key", this.key);
        builder.append("description", this.description);
        builder.append("metadata", this.metadata);
        return builder.toString();
    }
}