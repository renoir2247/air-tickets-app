package com.aviacassa.repository;

import com.aviacassa.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumberAndDepartureTime(String flightNumber, LocalDateTime departureTime);
}
