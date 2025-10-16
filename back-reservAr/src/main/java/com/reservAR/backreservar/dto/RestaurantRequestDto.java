package com.reservAR.backreservar.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RestaurantRequestDto (
        @NotNull String name,
        @NotNull String address,
        @NotNull String city,
        BigDecimal price
){
}
