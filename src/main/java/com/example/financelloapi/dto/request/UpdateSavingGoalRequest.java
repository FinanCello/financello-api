package com.example.financelloapi.dto.request;

import java.time.LocalDate;

public record UpdateSavingGoalRequest(
        Float targetAmount,
        LocalDate dueDate
) {}