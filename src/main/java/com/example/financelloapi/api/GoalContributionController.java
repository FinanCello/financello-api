package com.example.financelloapi.api;

import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.service.GoalContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contribution")
public class GoalContributionController {
    private final GoalContributionService goalContributionService;


    @GetMapping("/history")
    public ResponseEntity<List<GoalContribution>> getHistorialGoalContribution() {
        return ResponseEntity.ok(goalContributionService.historyGoalContributions());
    }
    @GetMapping("/history/date")
    public ResponseEntity<List<GoalContribution>> getHistorialGoalContributionByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(goalContributionService.historyGoalContributionsByDate(date));
    }

}
