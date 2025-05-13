package com.example.financelloapi.dto.test;

import com.example.financelloapi.model.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponse {

    private String firstName;
    private String lastName;
    private String email;

    public UserProfileResponse(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }
}