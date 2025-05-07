package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.Role;
import com.example.financelloapi.model.enums.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleType(RoleType roleType);
}