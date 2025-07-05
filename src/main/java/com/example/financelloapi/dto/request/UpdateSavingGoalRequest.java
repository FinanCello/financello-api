package com.example.financelloapi.dto.request;

import java.time.LocalDate;

public record UpdateSavingGoalRequest(
        String name,
        Float targetAmount,
        LocalDate dueDate
) {}