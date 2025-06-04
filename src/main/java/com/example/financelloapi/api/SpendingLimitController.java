package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.SpendingLimitRequest;
import com.example.financelloapi.dto.test.SpendingLimitAlertResponse;
import com.example.financelloapi.dto.test.SpendingLimitResponse;
import com.example.financelloapi.service.SpendingLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/spending-limit")

public class SpendingLimitController {
    private final SpendingLimitService spendingLimitService;

    @PostMapping
    public ResponseEntity<SpendingLimitResponse> addSpendingLimit(@RequestParam Integer userId, @Valid @RequestBody SpendingLimitRequest request) {
        SpendingLimitResponse response = spendingLimitService.createSpendingLimit(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<SpendingLimitAlertResponse>> getAlerts(@RequestParam Integer userId) {
        List<SpendingLimitAlertResponse> response = spendingLimitService.getAlerts(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SpendingLimitResponse>> listLimits(@PathVariable Integer userId) {
        List<SpendingLimitResponse> limits = spendingLimitService.listSpendingLimits(userId);
        return ResponseEntity.ok(limits);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteLimit(@RequestParam Integer userId, @RequestParam Integer categoryId) {
        spendingLimitService.deleteByCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }

}
