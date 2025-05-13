package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.Role;
import com.example.financelloapi.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleType(RoleType roleType);  // Funcion para buscar un rol por tipo
}