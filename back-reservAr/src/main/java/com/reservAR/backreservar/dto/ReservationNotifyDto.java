package com.reservAR.backreservar.dto;


import java.time.Instant;


public record ReservationNotifyDto(
        String type,
        Long reservationId,
        String status,
        Instant date
) {
}
