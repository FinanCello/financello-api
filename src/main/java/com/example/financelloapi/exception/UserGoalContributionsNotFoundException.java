package com.example.financelloapi.exception;

public class UserGoalContributionsNotFoundException extends RuntimeException {
    public UserGoalContributionsNotFoundException(Integer userId, Integer goalId) {
        super("No se encontraron contribuciones para el usuario " + userId + " en la meta " + goalId);
    }
} 