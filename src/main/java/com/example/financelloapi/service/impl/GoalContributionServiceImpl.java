package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.exception.EmptyAmountException;
import com.example.financelloapi.exception.IncorrectDateException;
import com.example.financelloapi.mapper.GoalContributionMapper;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.service.GoalContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GoalContributionServiceImpl implements GoalContributionService {

    private final GoalContributionRepository goalContributionRepository;
    private final GoalContributionMapper goalContributionMapper;

    @Override
    public RegisterGoalContributionResponse registerGoalContribution(RegisterGoalContributionRequest request) {
        if (request.amount() <= 0) {
            throw new EmptyAmountException("Amount must be greater than zero");
        }

        GoalContribution goalContribution = new GoalContribution();
        goalContribution.setDate(request.date());
        goalContribution.setAmount(request.amount());

        goalContributionRepository.save(goalContribution);

        return goalContributionMapper.toResponse(goalContribution);
    }

    @Override
    public List<GoalContribution> historyGoalContributionsByDate(LocalDate date) {
        List<GoalContribution> contributions = goalContributionRepository.findGoalContributionsByDate(date);
        if (contributions.isEmpty()) {
            throw new IncorrectDateException();
        }
        return contributions;
    }

    @Override
    public List<GoalContribution> historyGoalContributions() {
        return goalContributionRepository.findAll();
    }
}