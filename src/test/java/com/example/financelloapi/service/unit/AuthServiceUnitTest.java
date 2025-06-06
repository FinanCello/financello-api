package com.example.financelloapi.service.unit;

import com.example.financelloapi.mapper.UserMapper;
import com.example.financelloapi.repository.RoleRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthServiceUnitTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private RoleRepository roleRepository;
    @InjectMocks private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}