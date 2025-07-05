package com.example.financelloapi.exception;

public class CurrentAmountExceedsTargetException extends RuntimeException {
    public CurrentAmountExceedsTargetException(String message) {
        super(message);
    }
}
