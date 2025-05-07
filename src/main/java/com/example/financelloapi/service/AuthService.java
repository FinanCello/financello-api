package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.test.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
}
