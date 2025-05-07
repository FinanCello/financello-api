package com.example.financelloapi.dto.request;

import com.example.financelloapi.model.entity.GoalContribution;

import java.time.LocalDate;

public record AddSavingGoalRequest(GoalContribution contribution, String name, Float targetAmount, Float currentAmount, LocalDate dueDate) {}
