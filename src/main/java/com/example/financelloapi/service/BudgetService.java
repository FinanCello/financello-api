package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.model.entity.Budget;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.BudgetRepository;
import com.example.financelloapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Budget createBudget(BudgetRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        boolean exists = budgetRepository.findByUser_IdAndPeriod(user.getId(), request.getPeriod()).isPresent();

        if (exists) {
            throw new IllegalArgumentException("Ya existe un presupuesto para ese período.");
        }

        Budget budget = new Budget();
        budget.setPeriod(request.getPeriod());
        budget.setTotalIncomePlanned(request.getTotalIncomePlanned());
        budget.setTotalOutcomePlanned(request.getTotalOutcomePlanned());
        budget.setUser(user);

        return budgetRepository.save(budget);
    }
}
