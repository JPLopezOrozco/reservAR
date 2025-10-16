package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.Availability;
import com.reservAR.backreservar.model.Restaurant;

import java.math.BigDecimal;
import java.util.List;

public record RestaurantResponseDto(
        String name,
        String address,
        String city,
        List<Availability> availabilities,
        BigDecimal price
) {
    public static RestaurantResponseDto of(Restaurant restaurant) {
        List<Availability> availabilities = restaurant.getAvailability()!= null ?
                restaurant.getAvailability().stream().toList() :
                null;

        return new RestaurantResponseDto(
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCity(),
                availabilities,
                restaurant.getPrice()
        );
    }
}
