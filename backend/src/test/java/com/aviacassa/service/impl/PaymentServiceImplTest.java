package com.aviacassa.service.impl;

import com.aviacassa.entity.Booking;
import com.aviacassa.entity.Payment;
import com.aviacassa.entity.enums.BookingStatus;
import com.aviacassa.entity.enums.PaymentMethod;
import com.aviacassa.entity.enums.PaymentStatus;
import com.aviacassa.exception.PaymentFailedException;
import com.aviacassa.exception.ValidationException;
import com.aviacassa.repository.BookingRepository;
import com.aviacassa.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void initPayment_shouldCreatePayment_whenBookingIsPending() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(10000))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.initPayment(1L, PaymentMethod.CREDIT_CARD);

        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        assertEquals(BigDecimal.valueOf(10000), result.getAmount());
        assertNotNull(result.getTransactionId());
    }

    @Test
    void initPayment_shouldThrowValidationException_whenBookingNotPending() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                paymentService.initPayment(1L, PaymentMethod.CREDIT_CARD)
        );
    }

    @Test
    void initPayment_shouldThrowValidationException_whenBookingExpired() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.PENDING)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                paymentService.initPayment(1L, PaymentMethod.CREDIT_CARD)
        );
    }

    @Test
    void processWebhook_shouldConfirmBooking_whenStatusPaid() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.PENDING)
                .build();
        booking.getPassengers().add(
                com.aviacassa.entity.Passenger.builder().booking(booking).build()
        );

        Payment payment = Payment.builder()
                .id(100L)
                .transactionId("tx-123")
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build();

        when(paymentRepository.findByTransactionId("tx-123")).thenReturn(Optional.of(payment));

        paymentService.processWebhook("tx-123", PaymentStatus.PAID);

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertNotNull(payment.getPaidAt());
        assertEquals(1, booking.getTickets().size());
    }

    @Test
    void processWebhook_shouldDoNothing_whenStatusUnchanged() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.PENDING)
                .build();

        Payment payment = Payment.builder()
                .id(100L)
                .transactionId("tx-789")
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build();

        when(paymentRepository.findByTransactionId("tx-789")).thenReturn(Optional.of(payment));

        paymentService.processWebhook("tx-789", PaymentStatus.PENDING);

        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
    }

    @Test
    void processWebhook_shouldThrowValidationException_whenBookingNotPendingForPaid() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.CONFIRMED)
                .build();

        Payment payment = Payment.builder()
                .id(100L)
                .transactionId("tx-999")
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build();

        when(paymentRepository.findByTransactionId("tx-999")).thenReturn(Optional.of(payment));

        assertThrows(com.aviacassa.exception.ValidationException.class, () ->
                paymentService.processWebhook("tx-999", PaymentStatus.PAID)
        );
    }

    @Test
    void processWebhook_shouldThrowPaymentFailedException_whenStatusFailed() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.PENDING)
                .build();

        Payment payment = Payment.builder()
                .id(100L)
                .transactionId("tx-456")
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build();

        when(paymentRepository.findByTransactionId("tx-456")).thenReturn(Optional.of(payment));

        assertThrows(PaymentFailedException.class, () ->
                paymentService.processWebhook("tx-456", PaymentStatus.FAILED)
        );

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }
}
