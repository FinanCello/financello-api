package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.enums.UserType;

public record AuthResponse(Integer id, String email, String firstName, String lastName, UserType userType, String token) {
}