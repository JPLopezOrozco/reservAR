package com.reservAR.backreservar.dto;

import java.time.Instant;

public record ReservationWsEvent(
        Long reservationId,
        String status,
        Long restaurantId,
        Instant start
) {}