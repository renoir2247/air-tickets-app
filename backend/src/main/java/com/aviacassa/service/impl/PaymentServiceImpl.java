package com.aviacassa.service.impl;

import com.aviacassa.entity.Booking;
import com.aviacassa.entity.Payment;
import com.aviacassa.entity.Ticket;
import com.aviacassa.entity.enums.BookingStatus;
import com.aviacassa.entity.enums.PaymentMethod;
import com.aviacassa.entity.enums.PaymentStatus;
import com.aviacassa.entity.enums.TicketStatus;
import com.aviacassa.exception.PaymentFailedException;
import com.aviacassa.exception.ValidationException;
import com.aviacassa.repository.BookingRepository;
import com.aviacassa.repository.PaymentRepository;
import com.aviacassa.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Payment initPayment(Long bookingId, PaymentMethod paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException("Бронирование не найдено"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ValidationException("Оплата доступна только для бронирований в статусе PENDING");
        }

        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Срок резервирования истёк (15 минут)");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .transactionId(UUID.randomUUID().toString())
                .amount(booking.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .paymentMethod(paymentMethod)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void processWebhook(String transactionId, PaymentStatus status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ValidationException("Платёж с указанным transactionId не найден"));

        if (payment.getStatus() == status) {
            return;
        }

        if (status == PaymentStatus.FAILED) {
            payment.setStatus(PaymentStatus.FAILED);
            throw new PaymentFailedException("Платёж не прошёл (получен FAILED от платёжной системы)");
        }

        payment.setStatus(status);

        if (status == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());

            Booking booking = payment.getBooking();

            if (booking.getStatus() != BookingStatus.PENDING) {
                throw new ValidationException("Бронирование не может быть подтверждено: неверный статус");
            }

            booking.setStatus(BookingStatus.CONFIRMED);

            List<Ticket> tickets = booking.getPassengers().stream()
                    .map(p -> Ticket.builder()
                            .booking(booking)
                            .passenger(p)
                            .ticketNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase())
                            .status(TicketStatus.ISSUED)
                            .build())
                    .toList();

            booking.getTickets().addAll(tickets);
        }
    }
}
