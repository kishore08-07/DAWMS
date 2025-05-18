package com.backend.dawms.exception;

public class DepreciationException extends RuntimeException {
    public DepreciationException(String message) {
        super(message);
    }

    public DepreciationException(String message, Throwable cause) {
        super(message, cause);
    }
} 