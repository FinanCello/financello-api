package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.model.entity.Budget;
import com.example.financelloapi.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    public Budget createBudget(@RequestBody BudgetRequest request) {
        return budgetService.createBudget(request);
    }
}
