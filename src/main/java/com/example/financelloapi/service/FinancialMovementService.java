package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.dto.test.TransactionResponse;

import java.util.List;

public interface FinancialMovementService {
    RegisterFinancialMovementResponse registerMovement(Integer userId, RegisterFinancialMovementRequest request);
    List<TransactionResponse> getMovementsByUserIdFiltered(Integer userId, String movementTypeName, Integer categoryId);
}