package com.reservAR.backreservar.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequestDto(
        @NotNull Long restaurantId,
        Long userId,
        @NotNull LocalDateTime startDate,
        int partySize
) {
}
