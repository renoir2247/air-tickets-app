package com.aviacassa.dto;

import java.time.LocalDate;

public record PassengerDto(
        String firstName,
        String lastName,
        String passportNumber,
        LocalDate dateOfBirth
) {
}
