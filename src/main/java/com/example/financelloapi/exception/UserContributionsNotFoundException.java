package com.example.financelloapi.exception;

public class UserContributionsNotFoundException extends RuntimeException {
    public UserContributionsNotFoundException(Integer userId) {
        super("No se encontraron contribuciones para el usuario con ID: " + userId);
    }
} 