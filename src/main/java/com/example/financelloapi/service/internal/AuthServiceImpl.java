package com.example.financelloapi.service.internal;

import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.UserAlreadyExistsException;
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
}
