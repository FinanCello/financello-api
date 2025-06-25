package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goals")
@PreAuthorize("hasRole('BASIC')")
public class SavingGoalController {

    private final SavingGoalService savingGoalService;

    // → ahora recibimos el userId por path
    @PostMapping("/add/{userId}")
    public ResponseEntity<AddSavingGoalResponse> add(
            @PathVariable Integer userId,
            @RequestBody AddSavingGoalRequest request) {
        return ResponseEntity.ok(
                savingGoalService.addSavingGoal(userId, request)
        );
    }

    // opcional: endpoint para listar por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddSavingGoalResponse>> listByUser(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(
                savingGoalService.getGoalsByUser(userId)
        );
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
