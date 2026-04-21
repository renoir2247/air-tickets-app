package com.aviacassa.dto;

import java.time.LocalDateTime;

public record BookingStatusDTO(
        Long id,
        String status,
        LocalDateTime expiresAt
) {
}
