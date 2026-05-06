package com.aviacassa.service.impl;

import com.aviacassa.dto.PassengerDto;
import com.aviacassa.entity.Booking;
import com.aviacassa.entity.Flight;
import com.aviacassa.entity.enums.BookingStatus;
import com.aviacassa.exception.InsufficientSeatsException;
import com.aviacassa.exception.ValidationException;
import com.aviacassa.repository.BookingRepository;
import com.aviacassa.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_shouldCreateBooking_whenSeatsAvailable() {
        Flight flight = Flight.builder()
                .id(1L)
                .flightNumber("SU100")
                .origin("MOW")
                .destination("LED")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .seatsQuota(50)
                .availableSeats(10)
                .basePrice(BigDecimal.valueOf(5000))
                .build();

        PassengerDto passenger = new PassengerDto("Иван", "Иванов", "123456", LocalDate.of(1990, 1, 1));

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(1L, List.of(passenger));

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.valueOf(5000), result.getTotalAmount());
        assertEquals(1, result.getPassengers().size());
        assertEquals(9, flight.getAvailableSeats());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowValidationException_whenPassengersEmpty() {
        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, Collections.emptyList())
        );
    }

    @Test
    void createBooking_shouldThrowInsufficientSeatsException_whenNotEnoughSeats() {
        Flight flight = Flight.builder()
                .id(1L)
                .availableSeats(1)
                .basePrice(BigDecimal.valueOf(1000))
                .build();

        PassengerDto p1 = new PassengerDto("A", "B", "111", LocalDate.of(1990, 1, 1));
        PassengerDto p2 = new PassengerDto("C", "D", "222", LocalDate.of(1991, 1, 1));

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        assertThrows(InsufficientSeatsException.class, () ->
                bookingService.createBooking(1L, List.of(p1, p2))
        );
    }

    @Test
    void cancelBooking_shouldCancel_andRestoreSeats() {
        Flight flight = Flight.builder()
                .id(1L)
                .availableSeats(5)
                .build();

        Booking booking = Booking.builder()
                .id(10L)
                .status(BookingStatus.PENDING)
                .flight(flight)
                .build();
        booking.getPassengers().add(
                com.aviacassa.entity.Passenger.builder().booking(booking).build()
        );

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(10L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(6, flight.getAvailableSeats());
    }

    @Test
    void findAll_shouldReturnAllBookings() {
        Booking booking = Booking.builder().id(1L).status(BookingStatus.PENDING).build();
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<Booking> result = bookingService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void findById_shouldReturnBooking_whenExists() {
        Booking booking = Booking.builder().id(1L).status(BookingStatus.CONFIRMED).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findById_shouldThrowValidationException_whenNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () ->
                bookingService.findById(99L)
        );
    }

    @Test
    void cancelBooking_shouldThrowValidationException_whenAlreadyConfirmed() {
        Booking booking = Booking.builder()
                .id(10L)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.cancelBooking(10L)
        );
    }
}
