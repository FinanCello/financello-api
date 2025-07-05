package com.example.financelloapi.exception;

public class TargetAmountLessThanCurrentAmountException extends RuntimeException {
    public TargetAmountLessThanCurrentAmountException() {
        super("Target amount less than current amount");
    }
    
    public TargetAmountLessThanCurrentAmountException(String message) {
        super(message);
    }
}
