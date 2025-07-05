package com.example.financelloapi.dto.response;

public record BudgetStatusResponse(
        Integer budgetId,
        String budgetName,
        String period,
        Integer daysRemaining,
        Float remainingIncomeNeeded,
        Float remainingOutcomeAllowed,
        Float totalIncomePlanned,
        Float totalOutcomePlanned
) {
}
