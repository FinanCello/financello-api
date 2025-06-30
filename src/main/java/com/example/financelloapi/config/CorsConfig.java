package com.example.financelloapi.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsConfig {
    // CORS configuration is now handled by SecurityConfig to avoid conflicts
    // The SecurityConfig.corsConfigurationSource() method manages all CORS settings
}

