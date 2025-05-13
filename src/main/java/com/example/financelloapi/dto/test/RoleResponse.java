package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.entity.Role;

public record RoleResponse(Integer id, String roleType) {
    public RoleResponse(Role role) {
        this(role.getId(), role.getRoleType().name());
    }
}