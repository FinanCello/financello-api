package com.example.financelloapi.service;


import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;

public interface FinancialMovementService {
    RegisterFinancialMovementResponse registerMovement(RegisterFinancialMovementRequest request);
}
