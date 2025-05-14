package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.request.UpdateProfileRequest;
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.dto.test.UserProfileResponse;  // Importamos el DTO de respuesta de perfil
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.EmptyException;
import com.example.financelloapi.exception.UserAlreadyExistsException;
import com.example.financelloapi.exception.UserNotFoundException;
import com.example.financelloapi.mapper.UserMapper;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.AuthService;
import com.example.financelloapi.model.enums.RoleType;
import com.example.financelloapi.repository.RoleRepository;
import com.example.financelloapi.model.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (request.firstName().trim().isEmpty() || request.lastName().trim().isEmpty() || request.email().trim().isEmpty() || request.password().trim().isEmpty() || request.userType()==null) {
            throw new EmptyException("Fill all blank spaces");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        boolean nameExists = userRepository
                .findAll()
                .stream()
                .anyMatch(user ->
                        user.getFirstName().equalsIgnoreCase(request.firstName()) &&
                                user.getLastName().equalsIgnoreCase(request.lastName())
                );

        if (nameExists) {
            throw new CustomException("Username already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUserType(request.userType());

        // Asignar rol por defecto (BASIC)
        Role defaultRole = roleRepository.findByRoleType(RoleType.BASIC).orElseThrow(() -> new CustomException("Default role BASIC not found"));

        user.setRole(defaultRole);
        userRepository.save(user);

        return userMapper.toAuthResponse(user);
    }
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new UserNotFoundException(request.email()));

        if (!user.getPassword().equals(request.password())) {
            throw new CustomException("Incorrect password");
        }

        return userMapper.toAuthResponse(user);
    }

    // Implementación para obtener el perfil del usuario
    @Override
    public UserProfileResponse getUserProfile(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
        return new UserProfileResponse(user);
    }

    // Implementación para actualizar el perfil del usuario
    @Override
    public UserProfileResponse updateUserProfile(Integer userId, UpdateProfileRequest updateRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
        if (updateRequest.getFirstName().trim().isEmpty() || updateRequest.getLastName().trim().isEmpty() || updateRequest.getEmail().trim().isEmpty() || updateRequest.getPassword().trim().isEmpty()) {
            throw new EmptyException("Fill all blank spaces");
        }
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setPassword(updateRequest.getPassword());  // Asegúrate de manejar la seguridad de la contraseña
        userRepository.save(user);
        return new UserProfileResponse(user);
    }
}