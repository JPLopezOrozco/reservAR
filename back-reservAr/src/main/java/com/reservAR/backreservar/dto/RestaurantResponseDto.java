package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.Restaurant;

import java.math.BigDecimal;
import java.util.List;

public record RestaurantResponseDto(
        String name,
        String address,
        String city,
        List<AvailabilityResponseDto> availabilities,
        BigDecimal price
) {
    public static RestaurantResponseDto of(Restaurant restaurant) {
        List<AvailabilityResponseDto> availabilities = restaurant.getAvailability()!= null ?
                restaurant.getAvailability().stream()
                        .map(AvailabilityResponseDto::of)
                        .toList() : null;

        return new RestaurantResponseDto(
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCity(),
                availabilities,
                restaurant.getPrice()
        );
    }
}
