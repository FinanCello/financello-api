package com.example.financelloapi.dto.test;

import java.time.LocalDate;

public record AddSavingGoalResponse(String name, Float targetAmount, LocalDate dueDate) {}
