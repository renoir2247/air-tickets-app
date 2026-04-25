package com.aviacassa.controller;

import com.aviacassa.dto.PaymentDTO;
import com.aviacassa.dto.PaymentInitRequest;
import com.aviacassa.dto.PaymentWebhookDTO;
import com.aviacassa.mapper.PaymentMapper;
import com.aviacassa.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> initPayment(@Valid @RequestBody PaymentInitRequest request) {
        var payment = paymentService.initPayment(request.getBookingId(), request.getPaymentMethod());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDto(payment));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> processWebhook(@Valid @RequestBody PaymentWebhookDTO request) {
        paymentService.processWebhook(request.getTransactionId(), request.getStatus());
        return ResponseEntity.ok().build();
    }
}
