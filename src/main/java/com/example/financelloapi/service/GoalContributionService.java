package com.example.financelloapi.service;

import com.example.financelloapi.model.entity.GoalContribution;
import java.time.LocalDate;
import java.util.List;

public interface GoalContributionService {
    List<GoalContribution> historyGoalContributionsByDate(LocalDate date);
    List<GoalContribution> historyGoalContributions();
}
