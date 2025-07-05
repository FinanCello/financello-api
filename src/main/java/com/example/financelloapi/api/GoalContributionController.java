package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.service.GoalContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contribution")
@PreAuthorize("hasRole('BASIC')")
public class GoalContributionController {
    private final GoalContributionService goalContributionService;

    @PostMapping("/register")
    public ResponseEntity<RegisterGoalContributionResponse> register(@RequestBody RegisterGoalContributionRequest request) {
        return ResponseEntity.ok(goalContributionService.registerGoalContribution(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<GoalContribution>> getHistorialGoalContribution() {
        return ResponseEntity.ok(goalContributionService.historyGoalContributions());
    }

    @GetMapping("/history/date")
    public ResponseEntity<List<GoalContribution>> getHistorialGoalContributionByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(goalContributionService.historyGoalContributionsByDate(date));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalContribution>> getContributionsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(goalContributionService.getContributionsByUserId(userId));
    }

    @GetMapping("/user/{userId}/goal/{goalId}")
    public ResponseEntity<List<GoalContribution>> getContributionsByUserIdAndGoalId(
            @PathVariable Integer userId, 
            @PathVariable Integer goalId) {
        return ResponseEntity.ok(goalContributionService.getContributionsByUserIdAndGoalId(userId, goalId));
    }
}
