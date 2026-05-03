package com.aviacassa.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test/validation")
        public void throwValidation() {
            throw new ValidationException("Неверные данные");
        }

        @GetMapping("/test/seats")
        public void throwSeats() {
            throw new InsufficientSeatsException("Нет мест");
        }

        @GetMapping("/test/payment")
        public void throwPayment() {
            throw new PaymentFailedException("Платёж не прошёл");
        }

        @GetMapping("/test/generic")
        public void throwGeneric() {
            throw new RuntimeException("Внутренняя ошибка");
        }
    }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleValidationException_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Неверные данные"));
    }

    @Test
    void handleInsufficientSeatsException_shouldReturnConflict() throws Exception {
        mockMvc.perform(get("/test/seats"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Нет мест"));
    }

    @Test
    void handlePaymentFailedException_shouldReturnBadGateway() throws Exception {
        mockMvc.perform(get("/test/payment"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Платёж не прошёл"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Внутренняя ошибка"));
    }
}
