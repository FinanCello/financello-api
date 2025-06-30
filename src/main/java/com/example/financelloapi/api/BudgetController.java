package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.dto.response.BudgetResponse;
import com.example.financelloapi.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/budgets")
@PreAuthorize("hasRole('BASIC')")
public class BudgetController {
    
    private final BudgetService budgetService;

    @PostMapping("/add")
    public ResponseEntity<BudgetResponse> createBudget(
            @RequestParam Integer userId, 
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByUserId(
            @RequestParam Integer userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUserId(userId));
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Integer budgetId) {
        return ResponseEntity.ok(budgetService.getBudgetById(budgetId));
    }
}
