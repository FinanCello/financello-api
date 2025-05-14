package com.example.financelloapi.dto.test;

import java.time.YearMonth;

public record SpendingLimitResponse(
        String categoryName,
        Float monthlyLimit,
        YearMonth period
        ) {}
