package com.example.financelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AddSavingGoalRequest(
        @NotBlank(message = "El nombre es obligatorio") 
        String name, 
        @NotNull(message = "El monto objetivo es obligatorio")
        @Positive(message = "El monto objetivo debe ser positivo") 
        Float targetAmount, 
        @NotNull(message = "La fecha de vencimiento es obligatoria")
        LocalDate dueDate
) {}
