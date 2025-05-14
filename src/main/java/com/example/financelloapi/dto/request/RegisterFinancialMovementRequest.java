package com.example.financelloapi.dto.request;

import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.enums.CurrencyType;
import com.example.financelloapi.model.enums.MovementType;

import java.time.LocalDate;

public record RegisterFinancialMovementRequest(
        Float amount,
        LocalDate date,
        MovementType movementType,
        Category categoryId,
        CurrencyType currencyType,
        Integer userId
) {}