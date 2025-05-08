package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.RoleRequest;
import com.example.financelloapi.dto.test.RoleResponse;
import com.example.financelloapi.service.RoleService;
import com.example.financelloapi.model.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        Role createdRole = roleService.createRole(request);
        RoleResponse response = new RoleResponse(createdRole);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Integer roleId) {
        Role role = roleService.getRole(roleId);
        RoleResponse response = new RoleResponse(role);
        return ResponseEntity.ok(response);
    }
}