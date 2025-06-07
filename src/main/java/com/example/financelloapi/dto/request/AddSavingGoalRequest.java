package com.example.financelloapi.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record AddSavingGoalRequest(String name, @Positive Float targetAmount, @PositiveOrZero Float currentAmount, LocalDate dueDate) {}
