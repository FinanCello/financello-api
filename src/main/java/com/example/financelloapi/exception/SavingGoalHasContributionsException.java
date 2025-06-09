package com.example.financelloapi.exception;

public class SavingGoalHasContributionsException extends RuntimeException {
    public SavingGoalHasContributionsException(Integer goalId) {
        super("No se puede eliminar la meta " + goalId + ": tiene aportes registrados");
    }
}