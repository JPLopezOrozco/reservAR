package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.Reservation;
import com.reservAR.backreservar.model.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReservationResponseDto(
        Long id,
        Long restaurantId,
        String restaurant,
        BigDecimal price,
        Long userId,
        String userName,
        Instant start,
        Status status,
        List<TableResponseDto> tables
) {

    public static ReservationResponseDto of(Reservation reservation) {

        List<TableResponseDto> tables = reservation.getTables().stream()
                .map(TableResponseDto::of)
                .toList();

        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getRestaurant().getId(),
                reservation.getRestaurant().getName() + " - " + reservation.getUser().getSurname(),
                reservation.getRestaurant().getPrice(),
                reservation.getUser().getId(),
                reservation.getUser().getName(),
                reservation.getStart(),
                reservation.getStatus(),
                tables
        );
    }
}
