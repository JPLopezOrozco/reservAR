package com.reservAR.backreservar.controller;

import com.reservAR.backreservar.dto.ReservationRequestDto;
import com.reservAR.backreservar.dto.ReservationResponseDto;
import com.reservAR.backreservar.model.Reservation;
import com.reservAR.backreservar.service.IReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;



    @GetMapping("/id/{id}")
    public ResponseEntity<ReservationResponseDto> findById(@PathVariable Long id){
        Reservation reservation = reservationService.findById(id);
        return ResponseEntity.ok(ReservationResponseDto.of(reservation));
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> create(@RequestBody @Valid ReservationRequestDto reservationRequestDto){
        Reservation reservation = reservationService.save(reservationRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.getId())
                .toUri();

        return ResponseEntity.created(location).body(ReservationResponseDto.of(reservation));
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReservationResponseDto>> findByUser(@RequestParam String username){
        List<ReservationResponseDto> reservation = reservationService.findByUser(username).stream()
                .map(ReservationResponseDto::of)
                .toList();
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<ReservationResponseDto> cancel(@PathVariable Long id){
        Reservation reservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(ReservationResponseDto.of(reservation));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PutMapping("/completedReservation/{id}")
    public ResponseEntity<ReservationResponseDto> completedReservation(@PathVariable Long id){
        Reservation reservation = reservationService.completeReservation(id);
        return ResponseEntity.ok(ReservationResponseDto.of(reservation));
    }



}
