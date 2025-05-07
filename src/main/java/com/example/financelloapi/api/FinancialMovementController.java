package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.service.FinancialMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movements")
public class FinancialMovementController {
    private final FinancialMovementService financialMovementService;

    @PostMapping("/register")
    public ResponseEntity<RegisterFinancialMovementResponse> register(@RequestBody RegisterFinancialMovementRequest request) {
        return ResponseEntity.ok(financialMovementService.registerMovement(request));
    }
}
