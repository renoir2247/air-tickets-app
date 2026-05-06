package com.aviacassa.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "aviacassa-secret-key-2026-very-long-for-hs256-safety");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        UserDetails userDetails = User.builder()
                .username("admin")
                .password("pass")
                .authorities("ROLE_ADMIN")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("admin", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        UserDetails userDetails = User.builder()
                .username("admin")
                .password("pass")
                .authorities("ROLE_ADMIN")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forWrongUser() {
        UserDetails userDetails = User.builder()
                .username("admin")
                .password("pass")
                .authorities("ROLE_ADMIN")
                .build();

        String token = jwtService.generateToken(userDetails);

        UserDetails other = User.builder()
                .username("other")
                .password("pass")
                .authorities("ROLE_USER")
                .build();

        assertFalse(jwtService.isTokenValid(token, other));
    }
}
