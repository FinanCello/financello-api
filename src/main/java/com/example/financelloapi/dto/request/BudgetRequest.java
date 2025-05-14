package com.example.financelloapi.dto.request;

public record BudgetRequest(Integer userId, String period, Float totalIncomePlanned, Float totalOutcomePlanned) {
}
