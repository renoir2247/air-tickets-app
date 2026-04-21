package com.aviacassa.dto;

import com.aviacassa.entity.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookDTO {

    @NotBlank(message = "transactionId обязателен")
    private String transactionId;

    @NotNull(message = "Статус платежа обязателен")
    private PaymentStatus status;
}
