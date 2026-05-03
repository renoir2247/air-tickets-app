package com.aviacassa.controller;

import com.aviacassa.config.JwtService;
import com.aviacassa.dto.LoginRequest;
import com.aviacassa.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() throws Exception {
        UserDetails userDetails = User.builder()
                .username("admin")
                .password("hash")
                .authorities("ROLE_ADMIN")
                .build();

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(passwordEncoder.matches("admin123", "hash")).thenReturn(true);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("admin123")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturnUnauthorized_whenPasswordInvalid() throws Exception {
        UserDetails userDetails = User.builder()
                .username("admin")
                .password("hash")
                .authorities("ROLE_ADMIN")
                .build();

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("wrong")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
