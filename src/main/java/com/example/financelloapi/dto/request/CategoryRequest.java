package com.example.financelloapi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        String description) {}
