package com.example.financelloapi.service.internal;

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
    public final GoalContributionRepository goalContributionRepository;

    public List<GoalContribution> historyGoalContributionsByDate(LocalDate date) {
        return goalContributionRepository.findGoalContributionsByDate(date);
    }

    public List<GoalContribution> historyGoalContributions() {
        return goalContributionRepository.findAll();
    }
}
