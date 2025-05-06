package com.example.financelloapi.service.internal;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.UserAlreadyExistsException;
import com.example.financelloapi.exception.UserNotFoundException;
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
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new UserNotFoundException(request.email()));

        if (!user.getPassword().equals(request.password())) {
            throw new CustomException("Incorrect password");
        }

        return userMapper.toAuthResponse(user);
    }

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
}
