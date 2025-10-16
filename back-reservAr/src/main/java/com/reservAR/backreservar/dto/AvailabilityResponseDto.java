package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.Availability;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityResponseDto(
        Long restaurantId,
        String restaurantName,
        DayOfWeek dayOfWeek,
        LocalTime start,
        LocalTime end
) {
    public static AvailabilityResponseDto of(Availability availability) {
        return new AvailabilityResponseDto(
                availability.getRestaurant().getId(),
                availability.getRestaurant().getName(),
                availability.getDayOfWeek(),
                availability.getStart(),
                availability.getEnd()
        );
    }
}
