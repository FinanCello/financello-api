package com.example.financelloapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AchievementException extends RuntimeException {
    public AchievementException(String message) {
        super(message);
    }
}