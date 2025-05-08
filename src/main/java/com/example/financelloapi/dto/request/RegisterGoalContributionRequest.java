package com.example.financelloapi.dto.request;

import java.time.LocalDate;

public record RegisterGoalContributionRequest(Float amount, LocalDate date) {}
