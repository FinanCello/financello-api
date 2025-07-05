package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.model.entity.GoalContribution;

import java.time.LocalDate;
import java.util.List;

public interface GoalContributionService {
    RegisterGoalContributionResponse registerGoalContribution(RegisterGoalContributionRequest request);
    List<GoalContribution> historyGoalContributionsByDate(LocalDate date);
    List<GoalContribution> historyGoalContributions();
    List<GoalContribution> getContributionsByUserId(Integer userId);
    List<GoalContribution> getContributionsByUserIdAndGoalId(Integer userId, Integer goalId);
}
