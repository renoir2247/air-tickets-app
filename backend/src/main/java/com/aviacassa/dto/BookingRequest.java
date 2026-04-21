package com.aviacassa.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "ID рейса обязателен")
    private Long flightId;

    @NotEmpty(message = "Список пассажиров не может быть пустым")
    @Valid
    private List<PassengerRequest> passengers;
}
