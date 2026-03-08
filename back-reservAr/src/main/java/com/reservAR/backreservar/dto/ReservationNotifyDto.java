package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.Status;

import java.time.Instant;

public record ReservationNotifyDto(
        String type,
        Long reservationId,
        String status,
        Instant date
) {
}
