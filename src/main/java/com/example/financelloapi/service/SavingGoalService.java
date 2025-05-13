package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;

public interface SavingGoalService {
    AddSavingGoalResponse addSavingGoal(AddSavingGoalRequest addSavingGoalRequest);
}
