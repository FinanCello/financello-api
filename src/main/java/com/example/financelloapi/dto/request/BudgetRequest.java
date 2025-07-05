package com.example.financelloapi.dto.request;

public record BudgetRequest(String name, String period, Float totalIncomePlanned, Float totalOutcomePlanned) {
}
