package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.dto.response.UserGoalsWithContributionsResponse;

import java.util.List;

public interface SavingGoalService {
    AddSavingGoalResponse addSavingGoal(Integer userId, AddSavingGoalRequest request);
    void deleteSavingGoal(Integer goalId);
    AddSavingGoalResponse updateSavingGoal(Integer goalId, UpdateSavingGoalRequest request);
    List<AddSavingGoalResponse> getGoalsByUser(Integer userId);
    List<UserGoalsWithContributionsResponse> getUserGoalsWithContributions(Integer userId);
}
