package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import org.springframework.stereotype.Component;

@Component
public class FinancialMovementMapper {
    public RegisterFinancialMovementResponse toRegisterFinancialMovementResponse(FinancialMovement financialMovement){
        return new RegisterFinancialMovementResponse(financialMovement.getAmount(), financialMovement.getDate(), financialMovement.getMovementType(), new CategoryResponse(financialMovement.getCategory().getName(), financialMovement.getCategory().getDescription()), financialMovement.getCurrencyType());
    }

    public FinancialMovement toEntity(RegisterFinancialMovementRequest request, Category category){
        FinancialMovement financialMovement = new FinancialMovement();
        financialMovement.setAmount(request.amount());
        financialMovement.setDate(request.date());
        financialMovement.setMovementType(request.movementType());
        financialMovement.setCategory(category);
        financialMovement.setCurrencyType(request.currencyType());
        return financialMovement;
    }
}