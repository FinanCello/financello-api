package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.SpendingLimitRequest;
import com.example.financelloapi.dto.test.SpendingLimitAlertResponse;
import com.example.financelloapi.dto.test.SpendingLimitResponse;
import com.example.financelloapi.service.SpendingLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/spending-limit")
@PreAuthorize("hasRole('BASIC')")
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

}
