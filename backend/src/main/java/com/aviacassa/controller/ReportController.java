package com.aviacassa.controller;

import com.aviacassa.dto.ReportDTO;
import com.aviacassa.entity.enums.BookingStatus;
import com.aviacassa.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final BookingRepository bookingRepository;

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        var allBookings = bookingRepository.findAll();
        var filtered = allBookings.stream()
                .filter(b -> !b.getCreatedAt().toLocalDate().isBefore(from))
                .filter(b -> !b.getCreatedAt().toLocalDate().isAfter(to))
                .toList();

        var confirmed = filtered.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).toList();
        var pending = filtered.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).toList();
        var cancelled = filtered.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).toList();

        var revenue = confirmed.stream()
                .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var report = ReportDTO.builder()
                .from(from)
                .to(to)
                .totalBookings(filtered.size())
                .confirmedBookings(confirmed.size())
                .pendingBookings(pending.size())
                .cancelledBookings(cancelled.size())
                .totalRevenue(revenue)
                .build();

        return ResponseEntity.ok(report);
    }
}
