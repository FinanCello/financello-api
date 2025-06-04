package com.example.financelloapi.service.unit;

import com.example.financelloapi.repository.RoleRepository;
import com.example.financelloapi.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RoleServiceUnitTest {

    @Mock RoleRepository roleRepository;
    @InjectMocks RoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}
