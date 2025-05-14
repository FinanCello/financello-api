package com.example.financelloapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.YearMonth;

public record SpendingLimitRequest(
        @NotNull Integer categoryId,
        @NotNull @Min (value=1, message = "El limite debe de ser mayor a 0")
        Float monthlyLimit,
        @NotNull YearMonth period
    ){}
