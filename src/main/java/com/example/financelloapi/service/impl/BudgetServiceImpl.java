package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.model.entity.Budget;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.BudgetRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.BudgetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Budget createBudget(BudgetRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        boolean exists = budgetRepository.findByUser_IdAndPeriod(user.getId(), request.period()).isPresent();

        if (exists) {
            throw new IllegalArgumentException("Ya existe un presupuesto para ese período.");
        }

        Budget budget = new Budget();
        budget.setPeriod(request.period());
        budget.setTotalIncomePlanned(request.totalIncomePlanned());
        budget.setTotalOutcomePlanned(request.totalOutcomePlanned());
        budget.setUser(user);

        return budgetRepository.save(budget);
    }
}
