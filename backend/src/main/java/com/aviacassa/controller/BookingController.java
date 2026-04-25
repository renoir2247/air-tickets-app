package com.aviacassa.controller;

import com.aviacassa.dto.BookingDTO;
import com.aviacassa.dto.BookingRequest;
import com.aviacassa.dto.BookingStatusDTO;
import com.aviacassa.mapper.BookingMapper;
import com.aviacassa.service.IBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class BookingController {

    private final IBookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingRequest request) {
        var booking = bookingService.createBooking(
                request.getFlightId(),
                bookingMapper.passengerRequestListToPassengerDtoList(request.getPassengers())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.toDto(booking));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        var bookings = bookingService.findAll();
        return ResponseEntity.ok(bookingMapper.toDtoList(bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        var booking = bookingService.findById(id);
        return ResponseEntity.ok(bookingMapper.toDto(booking));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<BookingStatusDTO> getBookingStatus(@PathVariable Long id) {
        var booking = bookingService.findById(id);
        return ResponseEntity.ok(new BookingStatusDTO(booking.getId(), booking.getStatus().name(), booking.getExpiresAt()));
    }
}
