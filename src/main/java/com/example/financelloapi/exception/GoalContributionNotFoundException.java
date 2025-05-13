package com.example.financelloapi.exception;

public class GoalContributionNotFoundException extends RuntimeException {
    public GoalContributionNotFoundException(Integer id) {
        super("GoalContribution not found with id: " + id);
    }
}
