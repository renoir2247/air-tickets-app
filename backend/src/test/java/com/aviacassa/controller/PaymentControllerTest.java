package com.aviacassa.controller;

import com.aviacassa.dto.PaymentDTO;
import com.aviacassa.dto.PaymentInitRequest;
import com.aviacassa.dto.PaymentWebhookDTO;
import com.aviacassa.entity.Payment;
import com.aviacassa.entity.enums.PaymentMethod;
import com.aviacassa.entity.enums.PaymentStatus;
import com.aviacassa.exception.GlobalExceptionHandler;
import com.aviacassa.mapper.PaymentMapper;
import com.aviacassa.service.IPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IPaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void initPayment_shouldReturnCreated() throws Exception {
        Payment payment = Payment.builder()
                .id(1L)
                .transactionId("tx-123")
                .amount(BigDecimal.valueOf(10000))
                .build();

        when(paymentService.initPayment(any(), any())).thenReturn(payment);
        when(paymentMapper.toDto(any())).thenReturn(
                PaymentDTO.builder()
                        .id(1L)
                        .transactionId("tx-123")
                        .amount(BigDecimal.valueOf(10000))
                        .build()
        );

        PaymentInitRequest request = PaymentInitRequest.builder()
                .bookingId(1L)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("tx-123"));
    }

    @Test
    void processWebhook_shouldReturnOk() throws Exception {
        PaymentWebhookDTO request = PaymentWebhookDTO.builder()
                .transactionId("tx-123")
                .status(PaymentStatus.PAID)
                .build();

        mockMvc.perform(post("/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
