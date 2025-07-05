package com.example.financelloapi.exception;

public class ContributionExceedsTargetException extends RuntimeException {
    public ContributionExceedsTargetException(String message) {
        super(message);
    }
}
