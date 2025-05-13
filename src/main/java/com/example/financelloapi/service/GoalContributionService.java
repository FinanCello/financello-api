package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;

public interface GoalContributionService {
    RegisterGoalContributionResponse registerGoalContribution(RegisterGoalContributionRequest request);
}
