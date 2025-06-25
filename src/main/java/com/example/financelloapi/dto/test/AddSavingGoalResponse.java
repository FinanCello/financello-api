package com.example.financelloapi.dto.test;

import java.time.LocalDate;

public record AddSavingGoalResponse(
        Integer id,
        String name, 
        Float targetAmount, 
        Float currentAmount,
        LocalDate dueDate,
        Integer userId
) {}
