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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        if (request.date().isBefore(LocalDate.now())) {
            throw new CustomException("La fecha no puede estar en el pasado.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        List<FinancialMovement> existingMovements = financialMovementRepository
                .findByUser_IdAndMovementType(userId, request.movementType());

        boolean isDuplicate = existingMovements.stream().anyMatch(m ->
                Float.compare(m.getAmount(), request.amount()) == 0 &&
                        m.getDate().equals(request.date()) &&
                        m.getCategory().getId().equals(request.categoryId())
        );

        if (isDuplicate) {
            throw new CustomException("Movimiento duplicado");
        }

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

        try {
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

        } catch (DataAccessException e) {
            throw new CustomException("Error al cargar historial");
        }
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