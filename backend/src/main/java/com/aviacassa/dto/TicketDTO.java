package com.aviacassa.dto;

import com.aviacassa.entity.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    private Long id;
    private String ticketNumber;
    private TicketStatus status;
    private LocalDateTime issuedAt;
}
