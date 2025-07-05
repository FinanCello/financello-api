package com.example.financelloapi.exception;

public class UserGoalsNotFoundException extends RuntimeException {
    public UserGoalsNotFoundException(Integer userId) {
        super("No se encontraron metas de ahorro para el usuario con ID: " + userId);
    }
} 