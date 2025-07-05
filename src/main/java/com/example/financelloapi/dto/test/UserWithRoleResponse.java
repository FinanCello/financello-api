package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.enums.RoleType;

public record UserWithRoleResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        RoleType role
) {}