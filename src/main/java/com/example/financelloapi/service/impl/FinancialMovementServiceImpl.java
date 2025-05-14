package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.exception.CategoryNotFoundException;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.mapper.FinancialMovementMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.service.FinancialMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FinancialMovementServiceImpl implements FinancialMovementService {
    private final CategoryRepository categoryRepository;
    private final FinancialMovementRepository financialMovementRepository;
    private final FinancialMovementMapper financialMovementMapper;

    @Override
    public RegisterFinancialMovementResponse registerMovement(RegisterFinancialMovementRequest request) {
        if (request.amount() == null || request.amount().doubleValue() <= 0) {
            throw new CustomException("Amount must be greater than or equal to 0.");
        }

        Category category = categoryRepository.findByName(request.category().getName()).orElseThrow(() -> new CategoryNotFoundException(request.category().getName()));

        FinancialMovement movement = financialMovementMapper.toEntity(request, category);
        financialMovementRepository.save(movement);

        return financialMovementMapper.toRegisterFinancialMovementResponse(movement);
    }
}
