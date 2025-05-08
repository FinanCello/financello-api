package com.example.financelloapi.dto.request;

import com.example.financelloapi.model.enums.UserType;

public record RegisterRequest(String email, String password, String firstName, String lastName, UserType userType) {}
