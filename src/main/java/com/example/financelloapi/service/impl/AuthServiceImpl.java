package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.request.UpdateProfileRequest;
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.dto.test.UserProfileResponse;
import com.example.financelloapi.dto.test.UserWithRoleResponse;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.EmptyException;
import com.example.financelloapi.exception.UserAlreadyExistsException;
import com.example.financelloapi.exception.UserNotFoundException;
import com.example.financelloapi.mapper.UserMapper;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.security.JwtUtil;
import com.example.financelloapi.service.AuthService;
import com.example.financelloapi.model.enums.RoleType;
import com.example.financelloapi.repository.RoleRepository;
import com.example.financelloapi.model.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (request.firstName().trim().isEmpty() || request.lastName().trim().isEmpty() ||
                request.email().trim().isEmpty() || request.password().trim().isEmpty() ||
                request.userType() == null) {
            throw new EmptyException("Fill all blank spaces");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        boolean nameExists = userRepository.findAll().stream().anyMatch(user ->
                user.getFirstName().equalsIgnoreCase(request.firstName()) &&
                        user.getLastName().equalsIgnoreCase(request.lastName())
        );

        if (nameExists) {
            throw new CustomException("Username already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUserType(request.userType());

        // Asignar rol por defecto (BASIC)
        Role defaultRole = roleRepository.findByRoleType(RoleType.BASIC).orElseThrow(() -> new CustomException("No se pudo asignar rol automático"));

        user.setRole(defaultRole);

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail(),savedUser.getRole().toString());

        return new AuthResponse(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getUserType(), token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException("Incorrect password");
        }

        String encodeToken = jwtUtil.generateToken(user.getEmail(),user.getRole().toString());

        return new AuthResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType(),
                encodeToken
        );
    }

    @Override
    public UserProfileResponse getUserProfileAuthSecurity(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Error al cargar perfil"));

        if (user.getRole() == null || user.getRole().getRoleType() != RoleType.ADMIN) {
            throw new CustomException("Acceso denegado");
        }

        return new UserProfileResponse(user);
    }


    // Implementación para obtener el perfil del usuario
    @Override
    public UserProfileResponse getUserProfile(Integer userId) {
        // Si no existe, lanzamos UserNotFoundException con el mensaje “Error al cargar perfil”
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Error al cargar perfil"));

        return new UserProfileResponse(user);
    }

    // Implementación para actualizar el perfil del usuario
    @Override
    public UserProfileResponse updateUserProfile(Integer userId, UpdateProfileRequest updateRequest) {
        // 1) Buscamos al usuario; si no existe, lanzamos UserNotFoundException
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Error al cargar perfil"));

        // 2) Validamos que ninguno de los campos venga vacío o en blanco
        if (updateRequest.getFirstName() == null || updateRequest.getFirstName().isBlank()
                || updateRequest.getLastName()  == null || updateRequest.getLastName().isBlank()
                || updateRequest.getEmail()     == null || updateRequest.getEmail().isBlank()
                || updateRequest.getPassword()  == null || updateRequest.getPassword().isBlank()) {
            throw new EmptyException("Campo obligatorio");
        }

        // 3) Verificamos que el email nuevo no esté registrado en otro usuario
        userRepository.findByEmail(updateRequest.getEmail())
                .ifPresent(otro -> {
                    if (!otro.getId().equals(userId)) {
                        throw new CustomException("Email ya registrado");
                    }
                });

        // 4) Si llega hasta aquí, actualizamos campos y guardamos
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setPassword(updateRequest.getPassword());
        User saved = userRepository.save(user);

        return new UserProfileResponse(saved);
    }

    @Override
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("No autenticado");
        }
        return authentication.getName();
    }

    @Override
    public List<UserWithRoleResponse> getAllUsersWithRolesAuthSecurity() {
        String currentEmail = getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (currentUser.getRole() == null) {
            throw new CustomException("Rol no asignado");
        }

        if (currentUser.getRole().getRoleType() != RoleType.ADMIN) {
            throw new CustomException("Acceso denegado");
        }

        List<User> usuarios = userRepository.findAll();

        return usuarios.stream()
                .map(u -> {
                    Role role = u.getRole();
                    RoleType roleType = (role != null) ? role.getRoleType() : null;
                    return new UserWithRoleResponse(
                            u.getId(),
                            u.getFirstName(),
                            u.getLastName(),
                            u.getEmail(),
                            roleType
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserWithRoleResponse> getAllUsersWithRoles() {
        List<User> usuarios = userRepository.findAll();

        return usuarios.stream()
                .map(u -> {
                    Role role = u.getRole();
                    RoleType roleType = (role != null) ? role.getRoleType() : null;
                    return new UserWithRoleResponse(
                            u.getId(),
                            u.getFirstName(),
                            u.getLastName(),
                            u.getEmail(),
                            roleType
                    );
                })
                .collect(Collectors.toList());
    }
}