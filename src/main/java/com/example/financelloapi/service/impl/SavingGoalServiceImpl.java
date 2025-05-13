package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.exception.GoalContributionNotFoundException;
import com.example.financelloapi.exception.TargetAmountLessThanCurrentAmountException;
import com.example.financelloapi.mapper.SavingGoalMapper;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SavingGoalServiceImpl implements SavingGoalService{
    private final SavingGoalRepository savingGoalRepository;
    private final GoalContributionRepository goalContributionRepository;
    private final SavingGoalMapper savingGoalMapper;

    @Override
    public AddSavingGoalResponse addSavingGoal(AddSavingGoalRequest request) {
        if (request.targetAmount() <= request.currentAmount()) {
            throw new TargetAmountLessThanCurrentAmountException();
        }

        GoalContribution contribution = goalContributionRepository.findById(request.contribution().getId())
                .orElseThrow(() -> new GoalContributionNotFoundException(request.contribution().getId()));


        SavingGoal goal = savingGoalMapper.toEntity(request);
        goal.setContribution(contribution);
        goal.setCurrentAmount(0.0f);

        SavingGoal savedGoal = savingGoalRepository.save(goal);

        return savingGoalMapper.toResponse(savedGoal);
    }
}
