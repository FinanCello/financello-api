package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.request.UpdateProfileRequest;
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.dto.test.UserProfileResponse;
import com.example.financelloapi.dto.test.UserWithRoleResponse;

import java.util.List;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserProfileResponse getUserProfileAuthSecurity(Integer userId);
    UserProfileResponse getUserProfile(Integer userId);  // Funcion para obtener perfil
    UserProfileResponse updateUserProfile(Integer userId, UpdateProfileRequest updateRequest);

    String getCurrentUserEmail();
    List<UserWithRoleResponse> getAllUsersWithRolesAuthSecurity();
    List<UserWithRoleResponse> getAllUsersWithRoles();
}