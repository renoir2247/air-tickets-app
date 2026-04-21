package com.aviacassa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private LocalDate from;
    private LocalDate to;
    private int totalBookings;
    private int confirmedBookings;
    private int pendingBookings;
    private int cancelledBookings;
    private BigDecimal totalRevenue;
    private List<BookingDTO> bookings;
}
