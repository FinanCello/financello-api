package com.example.financelloapi.service;

<<<<<<< HEAD

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
=======
import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.dto.test.TransactionResponse;
>>>>>>> origin/develop
import com.example.financelloapi.model.enums.MovementType;

import java.util.List;

public interface FinancialMovementService {
<<<<<<< HEAD
    RegisterFinancialMovementResponse registerMovement(RegisterFinancialMovementRequest request);

=======
    RegisterFinancialMovementResponse registerMovement(Integer userId, RegisterFinancialMovementRequest request);
    List<TransactionResponse> getMovementsByUserIdFiltered(Integer userId, String movementTypeName, Integer categoryId);
>>>>>>> origin/develop
    List<RegisterFinancialMovementResponse> filterMovements(Integer userId, Integer categoryId, MovementType type);
}