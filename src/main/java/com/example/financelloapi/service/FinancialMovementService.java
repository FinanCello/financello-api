package com.example.financelloapi.service;

import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.mapper.TransactionMapper;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialMovementService {

    @Autowired
    private FinancialMovementRepository movementRepository;
    @Autowired
    private UserRepository userRepository;

    public List<TransactionResponse> getMovementsByUserIdFiltered(Integer id, String movementTypeName, Integer categoryId) {
        if (!userRepository.existsById(id)) {
            throw new UserDoesntExistException("User not found with ID: " + id);
        }

        List<FinancialMovement> movements;

        if (movementTypeName != null) {
            movements = movementRepository.findByUser_IdAndMovementTypeOrderByDateDesc(
                    id, MovementType.valueOf(movementTypeName.toUpperCase()));
        } else {
            movements = movementRepository.findByUser_IdOrderByDateDesc(id);
        }

        return movements.stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

}