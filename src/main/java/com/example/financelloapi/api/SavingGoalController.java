package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goals")
@PreAuthorize("hasRole('BASIC')")
public class SavingGoalController {
    private final SavingGoalService savingGoalService;

    @PostMapping("/add")
    public ResponseEntity<AddSavingGoalResponse> add(@RequestBody AddSavingGoalRequest request) {
        return ResponseEntity.ok(savingGoalService.addSavingGoal(request));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> delete(@PathVariable Integer goalId) {
        savingGoalService.deleteSavingGoal(goalId);
        return ResponseEntity.noContent().build();
    }
      
    @PutMapping("/{goalId}")
    public ResponseEntity<AddSavingGoalResponse> update(
            @PathVariable Integer goalId,
            @RequestBody UpdateSavingGoalRequest request) {
        return ResponseEntity.ok(savingGoalService.updateSavingGoal(goalId, request));
    }
}
