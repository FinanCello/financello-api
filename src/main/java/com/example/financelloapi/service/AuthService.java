package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.test.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
