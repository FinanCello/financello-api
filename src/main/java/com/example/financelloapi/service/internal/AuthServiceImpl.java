package com.example.financelloapi.service.internal;

import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.request.UpdateProfileRequest;
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.dto.test.UserProfileResponse;  // Importamos el DTO de respuesta de perfil
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.UserAlreadyExistsException;
import com.example.financelloapi.mapper.UserMapper;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
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

        userRepository.save(user);

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
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setPassword(updateRequest.getPassword());  // Asegúrate de manejar la seguridad de la contraseña
        userRepository.save(user);
        return new UserProfileResponse(user);
    }
}