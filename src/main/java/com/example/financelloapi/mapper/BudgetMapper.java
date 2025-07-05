package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.dto.response.BudgetResponse;
import com.example.financelloapi.model.entity.Budget;
import com.example.financelloapi.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public Budget toEntity(BudgetRequest request, User user) {
        Budget budget = new Budget();
        budget.setName(request.name());
        budget.setPeriod(request.period());
        budget.setTotalIncomePlanned(request.totalIncomePlanned());
        budget.setTotalOutcomePlanned(request.totalOutcomePlanned());
        budget.setUser(user);
        return budget;
    }

    public BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getName(),
                budget.getPeriod(),
                budget.getTotalIncomePlanned(),
                budget.getTotalOutcomePlanned(),
                budget.getUser().getId()
        );
    }
}
