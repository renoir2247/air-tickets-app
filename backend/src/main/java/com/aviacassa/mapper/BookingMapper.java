package com.aviacassa.mapper;

import com.aviacassa.dto.*;
import com.aviacassa.entity.Booking;
import com.aviacassa.entity.Passenger;
import com.aviacassa.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "flight.id", target = "flightId")
    @Mapping(source = "flight.flightNumber", target = "flightNumber")
    @Mapping(target = "passengerCount", expression = "java(booking.getPassengers() != null ? booking.getPassengers().size() : 0)")
    BookingDTO toDto(Booking booking);

    List<BookingDTO> toDtoList(List<Booking> bookings);

    PassengerDTO toPassengerDto(Passenger passenger);

    List<PassengerDTO> toPassengerDtoList(List<Passenger> passengers);

    TicketDTO toTicketDto(Ticket ticket);

    List<TicketDTO> toTicketDtoList(List<Ticket> tickets);

    PassengerDto passengerRequestToPassengerDto(PassengerRequest request);

    List<PassengerDto> passengerRequestListToPassengerDtoList(List<PassengerRequest> requests);
}
