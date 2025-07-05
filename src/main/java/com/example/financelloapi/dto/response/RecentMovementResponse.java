package com.example.financelloapi.dto.response;

import java.time.LocalDate;

public record RecentMovementResponse(
        Integer movementId,
        Float amount,
        LocalDate date,
        String categoryName,
        String categoryDescription,
        String movementType // "INCOME" o "OUTCOME"
) {
}
