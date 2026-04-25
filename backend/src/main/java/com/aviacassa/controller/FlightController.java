package com.aviacassa.controller;

import com.aviacassa.dto.FlightDTO;
import com.aviacassa.mapper.FlightMapper;
import com.aviacassa.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class FlightController {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        var flights = flightRepository.findAll();
        return ResponseEntity.ok(flightMapper.toDtoList(flights));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {
        var flights = flightRepository.findAll();
        var filtered = flights.stream()
                .filter(f -> origin == null || f.getOrigin().toLowerCase().contains(origin.toLowerCase()))
                .filter(f -> destination == null || f.getDestination().toLowerCase().contains(destination.toLowerCase()))
                .toList();
        return ResponseEntity.ok(flightMapper.toDtoList(filtered));
    }
}
