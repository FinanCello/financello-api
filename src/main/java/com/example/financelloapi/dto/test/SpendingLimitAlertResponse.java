package com.example.financelloapi.dto.test;

public record SpendingLimitAlertResponse(String categoryName, Float monthlyLimit, Float totalSpent, boolean overLimit, String alertMessage) {}
