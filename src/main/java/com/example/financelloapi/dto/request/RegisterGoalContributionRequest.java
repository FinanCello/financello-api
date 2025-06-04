package com.example.financelloapi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RegisterGoalContributionRequest(@Valid Integer goalId, @Positive Float amount, LocalDate date) {}
