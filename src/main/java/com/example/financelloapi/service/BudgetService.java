package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.dto.response.BudgetResponse;

import java.util.List;

public interface BudgetService {
    BudgetResponse createBudget(Integer userId, BudgetRequest request);
    List<BudgetResponse> getBudgetsByUserId(Integer userId);
    BudgetResponse getBudgetById(Integer budgetId);
}