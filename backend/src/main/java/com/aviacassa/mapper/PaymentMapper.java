package com.aviacassa.mapper;

import com.aviacassa.dto.PaymentDTO;
import com.aviacassa.entity.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDTO toDto(Payment payment);

    List<PaymentDTO> toDtoList(List<Payment> payments);
}
