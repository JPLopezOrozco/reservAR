package com.reservAR.backreservar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservAR.backreservar.dto.ReservationRequestDto;
import com.reservAR.backreservar.jwt.JwtFilter;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.Reservation;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.model.Status;
import com.reservAR.backreservar.model.TableEntity;
import com.reservAR.backreservar.model.User;
import com.reservAR.backreservar.service.CustomUserDetailsService;
import com.reservAR.backreservar.service.IReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IReservationService reservationService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailService;

    private Reservation buildReservation() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Don Julio")
                .price(new BigDecimal("5000"))
                .build();

        User user = User.builder()
                .id(2L)
                .name("Juan")
                .surname("Lopez")
                .build();

        TableEntity table = TableEntity.builder()
                .id(10L)
                .restaurant(restaurant)
                .code("M1")
                .minCapacity(2)
                .maxCapacity(4)
                .build();

        return Reservation.builder()
                .id(100L)
                .restaurant(restaurant)
                .user(user)
                .start(Instant.parse("2026-03-10T23:00:00Z"))
                .status(Status.BOOKED)
                .tables(List.of(table))
                .build();
    }

    @Test
    void shouldReturnReservationById() throws Exception {
        when(reservationService.findById(100L)).thenReturn(buildReservation());

        mockMvc.perform(get("/reservation/id/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.status").value("BOOKED"))
                .andExpect(jsonPath("$.tables.length()").value(1));
    }

    @Test
    void shouldCreateReservation() throws Exception {
        ReservationRequestDto request = new ReservationRequestDto(
                1L,
                null,
                LocalDateTime.of(2026, 3, 10, 20, 0),
                2
        );

        when(reservationService.save(any(ReservationRequestDto.class))).thenReturn(buildReservation());

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/reservation/100"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void shouldReturnReservationsByUser() throws Exception {
        when(reservationService.findByUser("juan@mail.com")).thenReturn(List.of(buildReservation()));

        mockMvc.perform(get("/reservation/user")
                        .param("username", "juan@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    void shouldCancelReservation() throws Exception {
        Reservation cancelled = buildReservation();
        cancelled.setStatus(Status.CANCELLED);

        when(reservationService.cancelReservation(100L)).thenReturn(cancelled);

        mockMvc.perform(put("/reservation/cancel/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}