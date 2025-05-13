package com.example.financelloapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandlerException extends RuntimeException {
    @ExceptionHandler(TargetAmountLessThanCurrentAmountException.class)
    public ResponseEntity<Map<String, String>> handleTargetAmountException(TargetAmountLessThanCurrentAmountException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(GoalContributionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGoalContributionNotFoundException(GoalContributionNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }
}
