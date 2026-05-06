package com.aviacassa.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorsConfigTest {

    @Test
    void corsConfigurer_shouldReturnWebMvcConfigurer() {
        CorsConfig config = new CorsConfig();
        assertNotNull(config.corsConfigurer());
    }
}
