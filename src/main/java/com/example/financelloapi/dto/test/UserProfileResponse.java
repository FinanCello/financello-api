package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.entity.User;
public record UserProfileResponse(
        String firstName,
        String lastName,
        String email
){
    public UserProfileResponse(User user) {
        this(user.getFirstName(), user.getLastName(), user.getEmail());
    }
}