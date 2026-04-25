package com.aviacassa.controller;

import com.aviacassa.config.JwtService;
import com.aviacassa.dto.AuthResponse;
import com.aviacassa.dto.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Неверные учётные данные");
        }

        String jwtToken = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}
