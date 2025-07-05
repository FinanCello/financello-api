package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public AuthResponse toAuthResponse(User user) {
        return new AuthResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword());
    }
}