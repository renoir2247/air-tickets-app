package com.aviacassa;

import com.aviacassa.dto.*;
import com.aviacassa.entity.*;
import com.aviacassa.entity.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoAndEntityTest {

    @Test
    void testAuthResponse() {
        AuthResponse dto = AuthResponse.builder().token("tkn").build();
        assertEquals("tkn", dto.getToken());
    }

    @Test
    void testBookingDTO() {
        BookingDTO dto = BookingDTO.builder()
                .id(1L)
                .bookingReference("REF123")
                .flightId(2L)
                .flightNumber("SU100")
                .status(BookingStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(5000))
                .passengerCount(2)
                .build();
        assertNotNull(dto.getId());
        assertEquals("SU100", dto.getFlightNumber());
    }

    @Test
    void testBookingRequest() {
        PassengerRequest p = PassengerRequest.builder()
                .firstName("A")
                .lastName("B")
                .passportNumber("123")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        BookingRequest req = BookingRequest.builder()
                .flightId(1L)
                .passengers(List.of(p))
                .build();
        assertEquals(1, req.getPassengers().size());
    }

    @Test
    void testBookingStatusDTO() {
        BookingStatusDTO dto = new BookingStatusDTO(1L, "PENDING", LocalDateTime.now());
        assertEquals("PENDING", dto.status());
    }

    @Test
    void testFlightDTO() {
        FlightDTO dto = FlightDTO.builder()
                .id(1L)
                .flightNumber("SU100")
                .origin("MOW")
                .destination("LED")
                .price(BigDecimal.valueOf(5000))
                .availableSeats(10)
                .build();
        assertEquals("MOW", dto.getOrigin());
    }

    @Test
    void testLoginRequest() {
        LoginRequest req = LoginRequest.builder().username("admin").password("pass").build();
        assertEquals("admin", req.getUsername());
    }

    @Test
    void testPassengerDtoRecord() {
        PassengerDto dto = new PassengerDto("A", "B", "123", LocalDate.of(1990, 1, 1));
        assertEquals("A", dto.firstName());
    }

    @Test
    void testPassengerDTO() {
        PassengerDTO dto = PassengerDTO.builder()
                .id(1L)
                .firstName("A")
                .lastName("B")
                .passportNumber("123")
                .build();
        assertEquals("A", dto.getFirstName());
    }

    @Test
    void testPassengerRequest() {
        PassengerRequest req = PassengerRequest.builder()
                .firstName("A")
                .lastName("B")
                .passportNumber("123")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        assertNotNull(req.getDateOfBirth());
    }

    @Test
    void testPaymentDTO() {
        PaymentDTO dto = PaymentDTO.builder()
                .id(1L)
                .transactionId("tx")
                .amount(BigDecimal.valueOf(100))
                .status(PaymentStatus.PAID)
                .build();
        assertEquals("tx", dto.getTransactionId());
    }

    @Test
    void testPaymentInitRequest() {
        PaymentInitRequest req = PaymentInitRequest.builder()
                .bookingId(1L)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();
        assertEquals(PaymentMethod.CREDIT_CARD, req.getPaymentMethod());
    }

    @Test
    void testPaymentWebhookDTO() {
        PaymentWebhookDTO dto = PaymentWebhookDTO.builder()
                .transactionId("tx")
                .status(PaymentStatus.PAID)
                .build();
        assertEquals("tx", dto.getTransactionId());
    }

    @Test
    void testReportDTO() {
        ReportDTO dto = ReportDTO.builder()
                .totalBookings(10)
                .confirmedBookings(5)
                .pendingBookings(3)
                .cancelledBookings(2)
                .totalRevenue(BigDecimal.valueOf(50000))
                .build();
        assertEquals(10, dto.getTotalBookings());
    }

    @Test
    void testTicketDTO() {
        TicketDTO dto = TicketDTO.builder()
                .id(1L)
                .ticketNumber("TKT123")
                .status(TicketStatus.ISSUED)
                .build();
        assertEquals("TKT123", dto.getTicketNumber());
    }

    @Test
    void testBookingEntity() {
        Booking b = Booking.builder()
                .id(1L)
                .bookingReference("REF")
                .status(BookingStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(1000))
                .build();
        assertEquals("REF", b.getBookingReference());
    }

    @Test
    void testFlightEntity() {
        Flight f = Flight.builder()
                .id(1L)
                .flightNumber("SU100")
                .origin("MOW")
                .destination("LED")
                .availableSeats(10)
                .basePrice(BigDecimal.valueOf(5000))
                .build();
        assertEquals("MOW", f.getOrigin());
    }

    @Test
    void testPassengerEntity() {
        Passenger p = Passenger.builder()
                .id(1L)
                .firstName("A")
                .lastName("B")
                .passportNumber("123")
                .build();
        assertEquals("A", p.getFirstName());
    }

    @Test
    void testPaymentEntity() {
        Payment p = Payment.builder()
                .id(1L)
                .transactionId("tx")
                .amount(BigDecimal.valueOf(100))
                .status(PaymentStatus.PENDING)
                .build();
        assertEquals("tx", p.getTransactionId());
    }

    @Test
    void testTicketEntity() {
        Ticket t = Ticket.builder()
                .id(1L)
                .ticketNumber("TKT")
                .status(TicketStatus.ISSUED)
                .build();
        assertEquals("TKT", t.getTicketNumber());
    }

    @Test
    void testUserEntity() {
        User u = User.builder()
                .id(1L)
                .username("admin")
                .password("pass")
                .role(Role.ROLE_ADMIN)
                .build();
        assertEquals(Role.ROLE_ADMIN, u.getRole());
    }
}
