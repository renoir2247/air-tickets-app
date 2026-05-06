package com.aviacassa;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest {
    @Test
    void testPasswordEncodeAndMatch() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "cashier123";
        String hash = encoder.encode(rawPassword);
        assertTrue(encoder.matches(rawPassword, hash), "Password should match");
    }

    @Test
    void testPasswordMismatch() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("correct-password");
        assertFalse(encoder.matches("wrong-password", hash), "Wrong password should not match");
    }
}
