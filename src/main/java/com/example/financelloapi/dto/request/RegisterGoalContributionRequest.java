package com.example.financelloapi.dto.request;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RegisterGoalContributionRequest(@Positive Float amount, LocalDate date) {}
