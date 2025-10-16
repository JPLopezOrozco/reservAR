package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.TableEntity;
import jakarta.persistence.Table;

public record TableResponseDto(
        Long restaurantId,
        String restaurantName,
        String code,
        int minCapacity,
        int maxCapacity
) {
    public static TableResponseDto of(TableEntity table) {
        return new TableResponseDto(
                table.getRestaurant().getId(),
                table.getRestaurant().getName(),
                table.getCode(),
                table.getMinCapacity(),
                table.getMaxCapacity()
        );
    }
}
