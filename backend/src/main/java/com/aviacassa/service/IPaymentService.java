package com.aviacassa.service;

import com.aviacassa.entity.Payment;
import com.aviacassa.entity.enums.PaymentMethod;
import com.aviacassa.entity.enums.PaymentStatus;

public interface IPaymentService {

    Payment initPayment(Long bookingId, PaymentMethod paymentMethod);

    void processWebhook(String transactionId, PaymentStatus status);
}
