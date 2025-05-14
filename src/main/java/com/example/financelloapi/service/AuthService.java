package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.request.UpdateProfileRequest;  // Importar el DTO de actualizar perfil
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.dto.test.UserProfileResponse;  // Importar el DTO de perfil

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserProfileResponse getUserProfile(Integer userId);  // Funcion para obtener perfil
    UserProfileResponse updateUserProfile(Integer userId, UpdateProfileRequest updateRequest);  // Funcion para actualizar perfil
}
