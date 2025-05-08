package com.example.financelloapi.exception;

public class EmptyAmountException extends RuntimeException {
    public EmptyAmountException(String message) {
        super(message);
    }
}
