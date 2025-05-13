package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.model.entity.SavingGoal;
import org.springframework.stereotype.Component;

@Component
public class SavingGoalMapper {
    public SavingGoal toEntity(AddSavingGoalRequest dto) {
        SavingGoal goal = new SavingGoal();
        goal.setName(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setDueDate(dto.dueDate());
        return goal;
    }

    public AddSavingGoalResponse toResponse(SavingGoal entity) {
        return new AddSavingGoalResponse(
                entity.getName(),
                entity.getTargetAmount(),
                entity.getDueDate()
        );
    }
}
