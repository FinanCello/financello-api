package com.example.financelloapi.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;  // Manejar la contraseña de forma segura, con hashing
}