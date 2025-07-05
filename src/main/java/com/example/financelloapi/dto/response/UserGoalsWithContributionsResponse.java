package com.example.financelloapi.dto.response;

import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.model.enums.SavingGoalProgress;

import java.time.LocalDate;
import java.util.List;

public record UserGoalsWithContributionsResponse(
    Integer goalId,
    String goalName,
    Float targetAmount,
    Float currentAmount,
    LocalDate dueDate,
    SavingGoalProgress progress,
    List<GoalContributionResponse> contributions
) {
    public static UserGoalsWithContributionsResponse fromSavingGoal(SavingGoal savingGoal) {
        List<GoalContributionResponse> contributions = savingGoal.getContributions() != null ?
            savingGoal.getContributions().stream()
                .map(GoalContributionResponse::fromGoalContribution)
                .toList() : List.of();

        return new UserGoalsWithContributionsResponse(
            savingGoal.getId(),
            savingGoal.getName(),
            savingGoal.getTargetAmount(),
            savingGoal.getCurrentAmount(),
            savingGoal.getDueDate(),
            savingGoal.getProgress(),
            contributions
        );
    }
}

record GoalContributionResponse(
    Integer contributionId,
    Float amount,
    LocalDate date
) {
    public static GoalContributionResponse fromGoalContribution(GoalContribution contribution) {
        return new GoalContributionResponse(
            contribution.getId(),
            contribution.getAmount(),
            contribution.getDate()
        );
    }
} 