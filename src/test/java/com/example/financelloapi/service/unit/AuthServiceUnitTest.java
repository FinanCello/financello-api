package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.test.UserWithRoleResponse;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.UserNotFoundException;
import com.example.financelloapi.mapper.UserMapper;
import com.example.financelloapi.model.entity.Role;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.enums.RoleType;
import com.example.financelloapi.repository.RoleRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthServiceUnitTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private RoleRepository roleRepository;
    @InjectMocks private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("US21-CP01 - Acceso permitido: ADMIN puede ver usuarios")
    void getAllUsersWithRoles_allowsAdminAccess() {
        Integer currentUserId = 1;

        Role adminRole = new Role();
        adminRole.setRoleType(RoleType.ADMIN);
        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setRole(adminRole);

        User other = new User();
        other.setId(2);
        other.setFirstName("Ana");
        other.setLastName("Lopez");
        other.setEmail("ana@example.com");
        other.setRole(adminRole);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.findAll()).thenReturn(List.of(currentUser, other));

        List<UserWithRoleResponse> result = authService.getAllUsersWithRoles();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("US21-CP02 - Acceso denegado: USER intenta ver usuarios")
    void getAllUsersWithRoles_deniesNonAdmin() {
        Integer currentUserId = 2;

        Role basicRole = new Role();
        basicRole.setRoleType(RoleType.BASIC);

        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setRole(basicRole);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

        CustomException ex = assertThrows(CustomException.class, () ->
                authService.getUserProfile(currentUserId)
        );
        assertEquals("Acceso denegado", ex.getMessage());
    }

    @Test
    @DisplayName("US21-CP03 - Error: Usuario no encontrado")
    void getAllUsersWithRoles_userNotFound() {
        when(userRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                authService.getUserProfile(10)
        );
    }

    @Test
    @DisplayName("CP04 - Error: Rol no asignado")
    void getAllUsersWithRoles_roleNull() {
        Integer currentUserId = 3;

        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setRole(null);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

        assertThrows(CustomException.class, () ->
                authService.getAllUsersWithRoles()
        );
    }
}

