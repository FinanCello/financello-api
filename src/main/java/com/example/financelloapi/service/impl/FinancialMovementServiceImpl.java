package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.exception.CategoryNotFoundException;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.mapper.FinancialMovementMapper;
import com.example.financelloapi.mapper.TransactionMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.FinancialMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FinancialMovementServiceImpl implements FinancialMovementService {
    private final CategoryRepository categoryRepository;
    private final FinancialMovementRepository financialMovementRepository;
    private final FinancialMovementMapper financialMovementMapper;
    private final UserRepository userRepository;

    @Override
    public RegisterFinancialMovementResponse registerMovement(Integer userId, RegisterFinancialMovementRequest request) {
        if (request.amount() == null || request.amount().doubleValue() <= 0) {
            throw new CustomException("Amount must be greater than or equal to 0.");
        }

        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        FinancialMovement movement = financialMovementMapper.toEntity(request, category);
        movement.setUser(userRepository.getById(userId));
        financialMovementRepository.save(movement);

        return financialMovementMapper.toRegisterFinancialMovementResponse(movement);
    }
    @Override
    public List<TransactionResponse> getMovementsByUserIdFiltered(Integer id, String movementTypeName, Integer categoryId) {
        if (!userRepository.existsById(id)) {
            throw new UserDoesntExistException("User not found with ID: " + id);
        }

        List<FinancialMovement> movements;

        if (movementTypeName != null) {
            movements = financialMovementRepository.findByUser_IdAndMovementTypeOrderByDateDesc(
                    id, MovementType.valueOf(movementTypeName.toUpperCase()));
        } else {
            movements = financialMovementRepository.findByUser_IdOrderByDateDesc(id);
        }

        return movements.stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @Override
    public List<RegisterFinancialMovementResponse> filterMovements(Integer userId, Integer categoryId, MovementType type) {
        List<FinancialMovement> movimientos;

        if (categoryId != null && type != null) {
            movimientos = financialMovementRepository.findByUser_IdAndCategory_IdAndMovementType(userId, categoryId, type);
        } else if (categoryId != null) {
            movimientos = financialMovementRepository.findByUser_IdAndCategory_Id(userId, categoryId);
        } else if (type != null) {
            movimientos = financialMovementRepository.findByUser_IdAndMovementType(userId, type);
        } else {
            movimientos = financialMovementRepository.findByUser_Id(userId);
        }

        return movimientos.stream()
                .map(financialMovementMapper::toRegisterFinancialMovementResponse)
                .toList();
    }
}
