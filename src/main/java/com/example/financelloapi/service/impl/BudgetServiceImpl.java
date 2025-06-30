package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.dto.response.BudgetResponse;
import com.example.financelloapi.mapper.BudgetMapper;
import com.example.financelloapi.model.entity.Budget;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.BudgetRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.BudgetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetMapper budgetMapper;

    @Override
    @Transactional
    public BudgetResponse createBudget(Integer userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        boolean exists = budgetRepository.findByUser_IdAndPeriod(user.getId(), request.period()).isPresent();

        if (exists) {
            throw new IllegalArgumentException("Ya existe un presupuesto para ese período.");
        }

        Budget budget = budgetMapper.toEntity(request, user);
        Budget savedBudget = budgetRepository.save(budget);
        
        return budgetMapper.toResponse(savedBudget);
    }

    @Override
    public List<BudgetResponse> getBudgetsByUserId(Integer userId) {
        // Verificar que el usuario existe
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        
        List<Budget> budgets = budgetRepository.findByUser_Id(userId);
        return budgets.stream()
                .map(budgetMapper::toResponse)
                .toList();
    }

    @Override
    public BudgetResponse getBudgetById(Integer budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrado."));
        
        return budgetMapper.toResponse(budget);
    }


}
