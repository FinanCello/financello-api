package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.enums.CurrencyType;
import com.example.financelloapi.model.enums.MovementType;

import java.time.LocalDate;

public record RegisterFinancialMovementResponse(Float amount, LocalDate date, MovementType movementType, CategoryResponse categoryResponse, CurrencyType currencyType) {}
