package com.placement.placementmanagement.exception;

import java.time.Instant;
import java.util.Map;

public class ApiError {

    private final int status;
    private final String message;
    private final Map<String, String> errors;
    private final Instant timestamp;

    public ApiError(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}