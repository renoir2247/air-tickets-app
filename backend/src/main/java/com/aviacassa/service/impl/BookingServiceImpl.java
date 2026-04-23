package com.aviacassa.service.impl;

import com.aviacassa.dto.PassengerDto;
import com.aviacassa.entity.Booking;
import com.aviacassa.entity.Flight;
import com.aviacassa.entity.Passenger;
import com.aviacassa.entity.Ticket;
import com.aviacassa.entity.enums.BookingStatus;
import com.aviacassa.entity.enums.TicketStatus;
import com.aviacassa.exception.InsufficientSeatsException;
import com.aviacassa.exception.ValidationException;
import com.aviacassa.repository.BookingRepository;
import com.aviacassa.repository.FlightRepository;
import com.aviacassa.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    @Override
    @Transactional
    public Booking createBooking(Long flightId, List<PassengerDto> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            throw new ValidationException("Список пассажиров не может быть пустым");
        }
        if (passengers.size() > 9) {
            throw new ValidationException("Максимальное количество пассажиров — 9");
        }

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ValidationException("Рейс не найден"));

        if (flight.getAvailableSeats() < passengers.size()) {
            throw new InsufficientSeatsException("Недостаточно свободных мест на выбранном рейсе");
        }

        Booking booking = Booking.builder()
                .bookingReference(UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase())
                .flight(flight)
                .status(BookingStatus.PENDING)
                .totalAmount(flight.getBasePrice().multiply(BigDecimal.valueOf(passengers.size())))
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        List<Passenger> passengerEntities = passengers.stream()
                .map(dto -> Passenger.builder()
                        .booking(booking)
                        .firstName(dto.firstName())
                        .lastName(dto.lastName())
                        .passportNumber(dto.passportNumber())
                        .dateOfBirth(dto.dateOfBirth())
                        .build())
                .toList();

        booking.getPassengers().addAll(passengerEntities);

        flight.setAvailableSeats(flight.getAvailableSeats() - passengers.size());

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException("Бронирование не найдено"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new ValidationException("Подтверждённое бронирование нельзя отменить");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ValidationException("Бронирование уже отменено");
        }

        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getPassengers().size());

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getTickets().forEach(t -> t.setStatus(TicketStatus.CANCELLED));
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException("Бронирование не найдено"));
    }
}
