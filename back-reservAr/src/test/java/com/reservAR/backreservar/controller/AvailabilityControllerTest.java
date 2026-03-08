package com.reservAR.backreservar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservAR.backreservar.dto.AvailabilityRequestDto;
import com.reservAR.backreservar.jwt.JwtFilter;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.Availability;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.service.CustomUserDetailsService;
import com.reservAR.backreservar.service.IAvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAvailabilityService availabilityService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailService;

    @Test
    void shouldReturnAvailabilityById() throws Exception {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();
        Availability availability = Availability.builder()
                .id(1L)
                .restaurant(restaurant)
                .dayOfWeek(DayOfWeek.MONDAY)
                .start(LocalTime.of(12, 0))
                .end(LocalTime.of(15, 0))
                .build();

        when(availabilityService.findById(1L)).thenReturn(availability);

        mockMvc.perform(get("/availability/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.restaurantName").value("Don Julio"))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));
    }

    @Test
    void shouldCreateAvailability() throws Exception {
        AvailabilityRequestDto request = new AvailabilityRequestDto(
                1L, "MONDAY", LocalTime.of(12, 0), LocalTime.of(15, 0)
        );

        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();
        Availability availability = Availability.builder()
                .id(5L)
                .restaurant(restaurant)
                .dayOfWeek(DayOfWeek.MONDAY)
                .start(LocalTime.of(12, 0))
                .end(LocalTime.of(15, 0))
                .build();

        when(availabilityService.save(any(AvailabilityRequestDto.class))).thenReturn(availability);

        mockMvc.perform(post("/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/availability/5"))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));
    }

    @Test
    void shouldReturnAvailabilitiesByRestaurant() throws Exception {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();

        when(availabilityService.findByRestaurantId(1L)).thenReturn(List.of(
                Availability.builder()
                        .id(1L)
                        .restaurant(restaurant)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .start(LocalTime.of(12, 0))
                        .end(LocalTime.of(15, 0))
                        .build()
        ));

        mockMvc.perform(get("/availability/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].restaurantName").value("Don Julio"));
    }

    @Test
    void shouldDeleteAvailability() throws Exception {
        doNothing().when(availabilityService).deleteById(1L);

        mockMvc.perform(delete("/availability/delete/1"))
                .andExpect(status().isNoContent());
    }
}