package com.reservAR.backreservar.dto;

public record TableRequestDto(
        Long restaurantId,
        String code,
        int minCapacity,
        int maxCapacity
) {
}
