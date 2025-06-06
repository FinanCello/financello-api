package com.example.financelloapi.exception;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
