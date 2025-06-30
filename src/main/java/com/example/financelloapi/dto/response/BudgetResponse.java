package com.example.financelloapi.dto.response;

public record BudgetResponse(
        Integer id,
        String name,
        String period,
        Float totalIncomePlanned,
        Float totalOutcomePlanned,
        Integer userId
) {
}
