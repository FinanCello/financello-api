package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.service.FinancialMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
public class FinancialMovementController {

    private final FinancialMovementService financialMovementService;

    @GetMapping("/{userId}")
    public List<TransactionResponse> getMovements(@PathVariable Integer userId, @RequestParam(required = false) String type, @RequestParam(required = false) Integer categoryId) {
        return financialMovementService.getMovementsByUserIdFiltered(userId, type, categoryId);
    }
    @PostMapping("/register")
    public ResponseEntity<RegisterFinancialMovementResponse> register(@RequestParam Integer userId, @RequestBody RegisterFinancialMovementRequest request) {
        return ResponseEntity.ok(financialMovementService.registerMovement(userId, request));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<RegisterFinancialMovementResponse>> filter(@RequestParam Integer userId, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) MovementType type) {
        List<RegisterFinancialMovementResponse> result = financialMovementService.filterMovements(userId, categoryId, type);
        return ResponseEntity.ok(result);
    }

}