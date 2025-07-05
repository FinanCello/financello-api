package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.dto.response.UserGoalsWithContributionsResponse;
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

    // Crear nueva meta de ahorro
    @PostMapping("/add")
    public ResponseEntity<AddSavingGoalResponse> add(
            @RequestParam Integer userId,
            @RequestBody AddSavingGoalRequest request) {
        return ResponseEntity.ok(
                savingGoalService.addSavingGoal(userId, request)
        );
    }

    // Listar metas por usuario
    @GetMapping("/user")
    public ResponseEntity<List<AddSavingGoalResponse>> listByUser(
            @RequestParam Integer userId) {
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

    // Obtener metas de ahorro con sus contribuciones por usuario
    @GetMapping("/user/{userId}/contributions")
    public ResponseEntity<List<UserGoalsWithContributionsResponse>> getUserGoalsWithContributions(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(
                savingGoalService.getUserGoalsWithContributions(userId)
        );
    }
}
