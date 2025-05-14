package com.example.financelloapi.exception;

public class IncorrectDateException extends RuntimeException {
    public IncorrectDateException() {
        super("Incorrect date");
    }
}
