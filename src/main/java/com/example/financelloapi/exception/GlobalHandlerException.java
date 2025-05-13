package com.example.financelloapi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class GlobalHandlerException extends RuntimeException {
    public ResponseEntity<Map<String, String>> handleIncorrectDateException(IncorrectDateException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", ex.getMessage()));
    }
}
