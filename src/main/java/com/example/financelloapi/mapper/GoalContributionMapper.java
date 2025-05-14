package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.model.entity.GoalContribution;
import org.springframework.stereotype.Component;

@Component
public class GoalContributionMapper {
    public GoalContribution toEntity(RegisterGoalContributionRequest registerGoalContributionRequest) {
        GoalContribution goalContribution = new GoalContribution();
        goalContribution.setAmount(registerGoalContributionRequest.amount());
        goalContribution.setDate(registerGoalContributionRequest.date());

        return goalContribution;
    }

    public RegisterGoalContributionResponse toResponse(GoalContribution goalContribution) {
        return new RegisterGoalContributionResponse(
                goalContribution.getAmount(),
                goalContribution.getDate()
        );
    }
}
