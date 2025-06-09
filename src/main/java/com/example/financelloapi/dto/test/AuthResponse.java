package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.enums.UserType;

public record AuthResponse(String email, String firstName, String lastName, UserType userType) {
}