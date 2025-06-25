package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SavingGoalMapper {
    // sobrecarga que recibe el User
    public SavingGoal toEntity(AddSavingGoalRequest dto, User user) {
        SavingGoal goal = new SavingGoal();
        goal.setName(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setDueDate(dto.dueDate());
        goal.setUser(user);                // <<< vinculamos usuario
        return goal;
    }

    // (opcional) mantiene el método antiguo si lo usas en pruebas u otros flujos
    public SavingGoal toEntity(AddSavingGoalRequest dto) {
        return toEntity(dto, null);
    }

    public AddSavingGoalResponse toResponse(SavingGoal entity) {
        return new AddSavingGoalResponse(
                entity.getId(),
                entity.getName(),
                entity.getTargetAmount(),
                entity.getCurrentAmount(),
                entity.getDueDate(),
                entity.getUser() != null ? entity.getUser().getId() : null
        );
    }
}
