package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.LoginRequest;
import com.example.financelloapi.dto.request.RegisterRequest;
import com.example.financelloapi.dto.request.UpdateProfileRequest;
import com.example.financelloapi.dto.test.AuthResponse;
import com.example.financelloapi.dto.test.UserWithRoleResponse;
import com.example.financelloapi.dto.test.UserProfileResponse;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.EmptyException;
import com.example.financelloapi.exception.UserAlreadyExistsException;
import com.example.financelloapi.exception.UserNotFoundException;
import com.example.financelloapi.mapper.UserMapper;
import com.example.financelloapi.model.entity.Role;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.enums.RoleType;
import com.example.financelloapi.model.enums.UserType;
import com.example.financelloapi.repository.RoleRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.security.JwtUtil;
import com.example.financelloapi.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // US01: Registrar usuario
    @Test
    @DisplayName("US01-CP01 - Registro exitoso")
    void register_success() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "juan@example.com",
                "password123",
                "Juan",
                "Pérez",
                UserType.PERSONAL
        );

        Role basicRole = new Role(1, RoleType.BASIC);

        User user = new User();
        user.setEmail("juan@example.com");
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setPassword("password123");
        user.setRole(basicRole);
        user.setUserType(UserType.PERSONAL);

        AuthResponse mockResponse = new AuthResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType(),
                "encodeToken"
        );

        // Mocks
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(roleRepository.findByRoleType(RoleType.BASIC)).thenReturn(Optional.of(basicRole));
        when(passwordEncoder.encode("password123")).thenReturn("password123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleType().name())).thenReturn("encodeToken");
        when(userMapper.toAuthResponse(any(User.class))).thenReturn(mockResponse);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response, "El response no debería ser null");
        assertEquals("Juan", response.firstName());
        assertEquals("Pérez", response.lastName());
        assertEquals("juan@example.com", response.email());
        assertEquals(UserType.PERSONAL, response.userType());
        assertEquals("encodeToken", response.token());

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getFirstName().equals("Juan") &&
                        savedUser.getLastName().equals("Pérez") &&
                        savedUser.getEmail().equals("juan@example.com") &&
                        savedUser.getPassword().equals("password123") &&
                        savedUser.getRole().getRoleType() == RoleType.BASIC
        ));
    }

    @Test
    @DisplayName("US01-CP02 - Email ya exsistente")
    void register_fails_whenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("juan@example.com", "password123", "Juan", "Pérez", UserType.PERSONAL);

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("US01-CP03 - Usuario ya exsistente")
    void register_fails_whenUsernameAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("juan@example.com", "password123", "Juan", "Pérez", UserType.PERSONAL);

        User existingUser = new User();
        existingUser.setFirstName("Juan");
        existingUser.setLastName("Pérez");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        // Act & Assert
        assertThrows(CustomException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("US01-CP04 - Registro: Campo vacío")
    void register_fails_whenFieldsAreEmpty() {
        // Arrange
        RegisterRequest request = new RegisterRequest(" ", " ", "", "", null);

        // Act & Assert
        assertThrows(EmptyException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    // US02: Login User
    @Test
    @DisplayName("US02-CP01 - Login: Escenario Exitoso")
    void login_success() {
        // Arrange
        LoginRequest request = new LoginRequest("juan@example.com", "password123");

        // Creamos un rol básico
        Role role = new Role();
        role.setRoleType(RoleType.BASIC);

        // Creamos el usuario con el rol incluido
        User user = new User();
        user.setEmail("juan@example.com");
        user.setPassword("password123"); // texto plano, lo estamos mockeando
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setUserType(UserType.PERSONAL);
        user.setRole(role); // 👈 necesario

        // Mock del token
        String token = "encodeToken";

        // Respuesta esperada
        AuthResponse expectedResponse = new AuthResponse(
                user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserType(), token
        );

        // Mocks necesarios
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "password123")).thenReturn(true); // 👈 también necesario
        when(jwtUtil.generateToken("juan@example.com", "BASIC")).thenReturn(token);
        when(userMapper.toAuthResponse(user)).thenReturn(expectedResponse);

        // Act
        AuthResponse actualResponse = authService.login(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.email(), actualResponse.email());
        assertEquals(expectedResponse.firstName(), actualResponse.firstName());
        assertEquals(expectedResponse.lastName(), actualResponse.lastName());
        assertEquals(expectedResponse.userType(), actualResponse.userType());
        assertEquals(expectedResponse.token(), actualResponse.token());
    }

    @Test
    @DisplayName("US02-CP02 - Login: Usuario no entontrado")
    void login_fails_whenUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest("noexistente@example.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
        verify(userMapper, never()).toAuthResponse(any());
    }

    @Test
    @DisplayName("US02-CP02 - Login: Contraseña incorrecta")
    void login_fails_whenIncorrectPassword() {
        // Arrange
        LoginRequest request = new LoginRequest("juan@example.com", "wrongpassword");

        User user = new User();
        user.setEmail("juan@example.com");
        user.setPassword("correctpassword");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(CustomException.class, () -> authService.login(request));
        verify(userMapper, never()).toAuthResponse(any());
    }


    // US17: Ver perfil
    @Test
    @DisplayName("US17-CP01 - Obtener perfil exitoso")
    void getUserProfile_success() {
        // Arrange (DADO)
        Integer userId = 1;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setFirstName("Juan");
        mockUser.setLastName("Pérez");
        mockUser.setEmail("juan.perez@example.com");
        mockUser.setRole(new Role(1, RoleType.BASIC));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act (CUANDO)
        UserProfileResponse response = authService.getUserProfile(userId);

        // Assert (ENTONCES)
        assertNotNull(response);
        assertEquals("Juan", response.firstName());
        assertEquals("Pérez", response.lastName());
        assertEquals("juan.perez@example.com", response.email());
    }

    @Test
    @DisplayName("US17-CP02 - Obtener perfil inexistente")
    void getUserProfile_notFound() {
        // Arrange (DADO)
        Integer userId = 99;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert (CUANDO / ENTONCES)
        CustomException ex = assertThrows(
                CustomException.class,
                () -> authService.getUserProfile(userId),
                "Se esperaba CustomException cuando el perfil no exista"
        );
        assertEquals("Error al cargar perfil", ex.getMessage());
    }

    // US18: Editar perfil
    @Test
    @DisplayName("US18-CP01 - Editar perfil exitoso")
    void updateUserProfile_success() {
        // Arrange (DADO)
        Integer userId = 2;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("María");
        existingUser.setLastName("García");
        existingUser.setEmail("maria.old@example.com");
        existingUser.setPassword("oldpass");

        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setFirstName("María");
        updateRequest.setLastName("García");
        updateRequest.setEmail("maria.new@example.com");
        updateRequest.setPassword("newpass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // Simular que no existe otro usuario con el email nuevo
        when(userRepository.findByEmail("maria.new@example.com")).thenReturn(Optional.empty());
        // Simular guardado exitoso
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (CUANDO)
        UserProfileResponse updatedResponse = authService.updateUserProfile(userId, updateRequest);

        // Assert (ENTONCES)
        assertNotNull(updatedResponse);
        assertEquals("María", updatedResponse.firstName());
        assertEquals("García", updatedResponse.lastName());
        assertEquals("maria.new@example.com", updatedResponse.email());

        verify(userRepository, times(1)).save(argThat(user ->
                user.getId().equals(userId) &&
                        "María".equals(user.getFirstName()) &&
                        "García".equals(user.getLastName()) &&
                        "maria.new@example.com".equals(user.getEmail()) &&
                        "newpass".equals(user.getPassword())
        ));
    }

    @Test
    @DisplayName("US18-CP02 - Editar perfil: Email duplicado")
    void updateUserProfile_duplicateEmail() {
        // Arrange (DADO)
        Integer userId = 3;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Pedro");
        existingUser.setLastName("López");
        existingUser.setEmail("pedro.lopez@example.com");
        existingUser.setPassword("secret");

        // Supongamos que otro usuario ya usa ese email
        User otherUser = new User();
        otherUser.setId(10);
        otherUser.setEmail("nuevo@example.com");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Pedro");
        request.setLastName("López");
        request.setEmail("nuevo@example.com");  // duplicado
        request.setPassword("secret");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("nuevo@example.com")).thenReturn(Optional.of(otherUser));

        // Act & Assert (CUANDO / ENTONCES)
        CustomException ex = assertThrows(
                CustomException.class,
                () -> authService.updateUserProfile(userId, request),
                "Se esperaba CustomException por email duplicado"
        );
        assertEquals("Email ya registrado", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("US18-CP03 - Editar perfil: Campo vacío")
    void updateUserProfile_emptyField() {
        // Arrange (DADO)
        Integer userId = 4;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Ana");
        existingUser.setLastName("Martínez");
        existingUser.setEmail("ana@example.com");
        existingUser.setPassword("secret");

        UpdateProfileRequest badRequest = new UpdateProfileRequest();
        badRequest.setFirstName("");  // campo vacío
        badRequest.setLastName("Martínez");
        badRequest.setEmail("ana@example.com");
        badRequest.setPassword("newpass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act & Assert (CUANDO / ENTONCES)
        EmptyException ex = assertThrows(
                EmptyException.class,
                () -> authService.updateUserProfile(userId, badRequest),
                "Se esperaba EmptyException cuando algún campo esté vacío"
        );
        assertEquals("Campo obligatorio", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    //US20: Ver lista de usuarios con rol
    @Test
    @DisplayName("US20-CP01 - Listar usuarios con roles (éxito)")
    void getAllUsersWithRoles_success() {
        // Arrange (DADO)
        User user1 = new User();
        user1.setId(1);
        user1.setFirstName("Luis");
        user1.setLastName("Martínez");
        user1.setEmail("luis.martinez@example.com");
        user1.setRole(new Role(1, RoleType.ADMIN));

        User user2 = new User();
        user2.setId(2);
        user2.setFirstName("Carla");
        user2.setLastName("González");
        user2.setEmail("carla.gonzalez@example.com");
        user2.setRole(new Role(2, RoleType.BASIC));

        List<User> todos = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(todos);

        // Act (CUANDO)
        List<UserWithRoleResponse> result = authService.getAllUsersWithRoles();

        // Assert (ENTONCES)
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verificamos primer usuario
        UserWithRoleResponse resp1 = result.get(0);
        assertEquals(1, resp1.id());
        assertEquals("Luis", resp1.firstName());
        assertEquals("Martínez", resp1.lastName());
        assertEquals("luis.martinez@example.com", resp1.email());
        assertEquals(RoleType.ADMIN, resp1.role());

        // Verificamos segundo usuario
        UserWithRoleResponse resp2 = result.get(1);
        assertEquals(2, resp2.id());
        assertEquals("Carla", resp2.firstName());
        assertEquals("González", resp2.lastName());
        assertEquals("carla.gonzalez@example.com", resp2.email());
        assertEquals(RoleType.BASIC, resp2.role());
    }

    @Test
    @DisplayName("US20-CP02 - Listar usuarios: No hay usuarios registrados")
    void getAllUsersWithRoles_noUsers() {
        // Arrange (DADO)
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act (CUANDO)
        List<UserWithRoleResponse> result = authService.getAllUsersWithRoles();

        // Assert (ENTONCES)
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Se esperaba lista vacía cuando no hay usuarios");
    }

    @Test
    @DisplayName("US20-CP03 - Listar usuarios: Usuario sin rol definido")
    void getAllUsersWithRoles_userWithoutRole() {
        // Arrange (DADO)
        User userSinRol = new User();
        userSinRol.setId(5);
        userSinRol.setFirstName("Tomás");
        userSinRol.setLastName("Ruiz");
        userSinRol.setEmail("tomas.ruiz@example.com");
        userSinRol.setRole(null); // Sin rol asignado

        when(userRepository.findAll()).thenReturn(Collections.singletonList(userSinRol));

        // Act (CUANDO)
        List<UserWithRoleResponse> result = authService.getAllUsersWithRoles();

        // Assert (ENTONCES)
        assertNotNull(result);
        assertEquals(1, result.size());

        UserWithRoleResponse resp = result.get(0);
        assertEquals(5, resp.id());
        assertEquals("Tomás", resp.firstName());
        assertEquals("Ruiz", resp.lastName());
        assertEquals("tomas.ruiz@example.com", resp.email());
        assertNull(resp.role(), "Se esperaba null cuando el usuario no tiene rol definido");
    }

    @Test
    @DisplayName("US21-CP01 - Acceso permitido: ADMIN puede ver usuarios")
    void getAllUsersWithRoles_allowsAdminAccess() {
        Integer currentUserId = 1;

        Role adminRole = new Role();
        adminRole.setRoleType(RoleType.ADMIN);
        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setRole(adminRole);

        User other = new User();
        other.setId(2);
        other.setFirstName("Ana");
        other.setLastName("Lopez");
        other.setEmail("ana@example.com");
        other.setRole(adminRole);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.findAll()).thenReturn(List.of(currentUser, other));

        List<UserWithRoleResponse> result = authService.getAllUsersWithRoles();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("US21-CP02 - Acceso denegado: USER intenta ver usuarios")
    void getAllUsersWithRoles_deniesNonAdmin() {
        Integer currentUserId = 2;

        Role basicRole = new Role();
        basicRole.setRoleType(RoleType.BASIC);

        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setRole(basicRole);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

        CustomException ex = assertThrows(CustomException.class, () ->
                authService.getUserProfileAuthSecurity(currentUserId)
        );
        assertEquals("Acceso denegado", ex.getMessage());

    }

    @Test
    @DisplayName("US21-CP03 - Error: Usuario no encontrado")
    void getAllUsersWithRoles_userNotFound() {
        when(userRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                authService.getUserProfile(10)
        );
    }

    @Test
    @DisplayName("CP04 - Error: Rol no asignado")
    void getAllUsersWithRoles_roleNull() {
        Integer currentUserId = 3;

        User currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setRole(null);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

        assertThrows(CustomException.class, () ->
                authService.getAllUsersWithRolesAuthSecurity()
        );
    }
}