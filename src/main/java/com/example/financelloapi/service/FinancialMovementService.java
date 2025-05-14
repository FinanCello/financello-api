package com.example.financelloapi.service;


import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.model.enums.MovementType;

import java.util.List;

public interface FinancialMovementService {
    RegisterFinancialMovementResponse registerMovement(RegisterFinancialMovementRequest request);

    List<RegisterFinancialMovementResponse> filterMovements(Integer userId, Integer categoryId, MovementType type);
}