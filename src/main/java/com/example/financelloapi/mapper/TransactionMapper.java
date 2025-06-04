package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.model.entity.FinancialMovement;

public class TransactionMapper {
    public static TransactionResponse toResponse(FinancialMovement movement) {
        return new TransactionResponse(movement.getAmount(), movement.getCurrencyType().name(), movement.getDate(), movement.getMovementType().name(), movement.getCategory().getName());
    }
}