package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.request.UpdateProfileRequest;  // Importamos el nuevo DTO para editar perfil
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.dto.test.UserProfileResponse;  // Nuevo DTO para devolver el perfil
import com.example.financelloapi.dto.test.UserWithRoleResponse;
import com.example.financelloapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Endpoint para obtener el perfil de usuario
    @PreAuthorize("hasRole('BASIC')")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Integer userId) {
        UserProfileResponse profile = authService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // Endpoint para actualizar los datos del perfil
    @PreAuthorize("hasRole('BASIC')")
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@PathVariable Integer userId, @RequestBody UpdateProfileRequest updateRequest) {
        UserProfileResponse updatedProfile = authService.updateUserProfile(userId, updateRequest);
        return ResponseEntity.ok(updatedProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserWithRoleResponse>> getAllUsersWithRoles() {
                List<UserWithRoleResponse> lista = authService.getAllUsersWithRoles();
                return ResponseEntity.ok(lista);
    }
}