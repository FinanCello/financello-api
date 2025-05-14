package com.example.financelloapi.exception;

public class InvalidLimitAmountException extends RuntimeException {
    public InvalidLimitAmountException(String message) {
        super(message);
    }
}
