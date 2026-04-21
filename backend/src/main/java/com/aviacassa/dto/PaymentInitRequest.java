package com.aviacassa.dto;

import com.aviacassa.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitRequest {

    @NotNull(message = "ID бронирования обязателен")
    private Long bookingId;

    @NotNull(message = "Способ оплаты обязателен")
    private PaymentMethod paymentMethod;
}
