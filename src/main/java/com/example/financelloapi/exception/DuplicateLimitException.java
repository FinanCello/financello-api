package com.example.financelloapi.exception;

public class DuplicateLimitException extends RuntimeException {
    public DuplicateLimitException(String message) {
        super(message);
    }
}
