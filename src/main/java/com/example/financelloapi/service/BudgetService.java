package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.model.entity.Budget;

public interface BudgetService {
    Budget createBudget(BudgetRequest request);
}