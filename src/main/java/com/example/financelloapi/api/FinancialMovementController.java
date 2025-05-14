package com.example.financelloapi.api;

import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.service.FinancialMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/movements")
@RequiredArgsConstructor
public class FinancialMovementController {

    private final FinancialMovementService financialMovementService;

    @GetMapping
    public List<TransactionResponse> getMovements(
            @PathVariable Integer userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer categoryId
    ) {
        return financialMovementService.getMovementsByUserIdFiltered(userId, type, categoryId);
    }
}