package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.service.GoalContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contribution")
public class GoalContributionController {
    private final GoalContributionService goalContributionService;

    @PostMapping("/register")
    public ResponseEntity<RegisterGoalContributionResponse> register(@RequestBody RegisterGoalContributionRequest request) {
        return ResponseEntity.ok(goalContributionService.registerGoalContribution(request));
    }
}
