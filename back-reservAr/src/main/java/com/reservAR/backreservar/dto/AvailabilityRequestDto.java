package com.reservAR.backreservar.dto;

import java.time.LocalTime;

public record AvailabilityRequestDto(
        Long restaurantId,
        String dayOfWeek,
        LocalTime start,
        LocalTime end
) {
}
