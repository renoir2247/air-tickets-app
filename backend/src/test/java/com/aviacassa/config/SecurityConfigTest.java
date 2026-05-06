package com.aviacassa.config;

import com.aviacassa.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Test
    void testBeansCreation() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter, userDetailsService);

        assertNotNull(config.passwordEncoder());
        assertNotNull(config.authenticationProvider());
        assertNotNull(config.authenticationManager());
        assertNotNull(config.authenticationEntryPoint());
    }
}
