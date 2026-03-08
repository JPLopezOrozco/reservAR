package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.Restaurant;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentDto(
        Long reservationId
) {
}
