package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.enums.SavingGoalProgress;
import java.time.LocalDate;

public record AddSavingGoalResponse(
        Integer id,
        String name, 
        Float targetAmount, 
        Float currentAmount,
        LocalDate dueDate,
        Integer userId,
        SavingGoalProgress progress
) {}
