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

    @GetMapping("/alerts"
    public ResponseEntity<SpendingLimitAlertResponse>> getAlerts(@)

}
