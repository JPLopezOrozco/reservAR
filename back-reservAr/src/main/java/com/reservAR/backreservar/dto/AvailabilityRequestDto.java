package com.reservAR.backreservar.dto;

import java.time.LocalTime;

public record AvailabilityRequestDto(
        Long restaurantId,
        String DayOfWeek,
        LocalTime start,
        LocalTime end
) {
}
