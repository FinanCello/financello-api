package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.RoleRequest;
import com.example.financelloapi.exception.RoleDoesntExistException;
import com.example.financelloapi.exception.UserAlreadyExistsException;
import com.example.financelloapi.model.entity.Role;
import com.example.financelloapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(RoleRequest request) {
        Role role = new Role(null, request.roleType());
        return roleRepository.save(role);
    }

    public Role getRole(Integer roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new RoleDoesntExistException(roleId));
    }
}