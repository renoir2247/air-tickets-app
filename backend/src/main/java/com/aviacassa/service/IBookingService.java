package com.aviacassa.service;

import com.aviacassa.dto.PassengerDto;
import com.aviacassa.entity.Booking;

import java.util.List;

public interface IBookingService {

    Booking createBooking(Long flightId, List<PassengerDto> passengers);

    void cancelBooking(Long bookingId);

    List<Booking> findAll();

    Booking findById(Long bookingId);
}
