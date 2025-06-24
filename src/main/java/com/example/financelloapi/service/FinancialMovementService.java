package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.model.enums.MovementType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FinancialMovementService {
    RegisterFinancialMovementResponse registerMovement(Integer userId, RegisterFinancialMovementRequest request);
    List<TransactionResponse> getMovementsByUserIdFiltered(Integer userId, String movementTypeName, Integer categoryId);
    List<RegisterFinancialMovementResponse> filterMovements(Integer userId, Integer categoryId, MovementType type);
    void importFromExcel(MultipartFile file);
}