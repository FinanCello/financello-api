package com.example.financelloapi.dto.response;

public record CategoryTotalResponse(
        Integer categoryId,
        String categoryName,
        Float totalAmount,
        String movementType // "INCOME" o "OUTCOME"
) {
}
