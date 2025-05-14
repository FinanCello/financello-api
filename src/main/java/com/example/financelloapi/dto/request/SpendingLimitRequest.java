package com.example.financelloapi.dto.request;

public record SpendingLimitRequest(Integer categoryId, Float monthlyLimit, Integer userId) {
}
