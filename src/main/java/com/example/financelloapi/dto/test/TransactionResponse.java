package com.example.financelloapi.dto.test;

import java.time.LocalDate;

public record TransactionResponse(Float amount, String currencyName, LocalDate date, String movementName, String categoryName) {
}