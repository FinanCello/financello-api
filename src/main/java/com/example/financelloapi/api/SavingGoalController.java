package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goals")
public class SavingGoalController {
    private final SavingGoalService savingGoalService;

    @PostMapping("/add")
    public ResponseEntity<AddSavingGoalResponse> add(@RequestBody AddSavingGoalRequest request) {
        return ResponseEntity.ok(savingGoalService.addSavingGoal(request));
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<AddSavingGoalResponse> update(
            @PathVariable Integer goalId,
            @RequestBody UpdateSavingGoalRequest request) {
        return ResponseEntity.ok(savingGoalService.updateSavingGoal(goalId, request));
    }
}
