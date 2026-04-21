package com.aviacassa.dto;

import com.aviacassa.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long id;
    private String bookingReference;
    private Long flightId;
    private String flightNumber;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private List<PassengerDTO> passengers;
    private List<TicketDTO> tickets;
    private Integer passengerCount;
}
