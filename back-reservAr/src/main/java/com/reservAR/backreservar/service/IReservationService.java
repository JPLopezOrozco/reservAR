package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.ReservationRequestDto;
import com.reservAR.backreservar.model.Reservation;

import java.util.List;

public interface IReservationService {
    Reservation findById(Long id);
    Reservation save(ReservationRequestDto reservation);
    List<Reservation> findByUser(Long id);
    Reservation cancelReservation(Long id);
    Reservation completeReservation(Long id);
    void expiredReservation();
}
