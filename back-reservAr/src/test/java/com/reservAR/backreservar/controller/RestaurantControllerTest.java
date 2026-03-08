package com.reservAR.backreservar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservAR.backreservar.dto.RestaurantRequestDto;
import com.reservAR.backreservar.jwt.JwtFilter;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.service.CustomUserDetailsService;
import com.reservAR.backreservar.service.IRestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRestaurantService restaurantService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailService;


    @Test
    void shouldReturnRestaurantById() throws Exception {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Don Julio")
                .address("Calle 123")
                .city("CABA")
                .price(new BigDecimal("5000"))
                .build();

        when(restaurantService.findById(1L)).thenReturn(restaurant);

        mockMvc.perform(get("/restaurants/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Don Julio"))
                .andExpect(jsonPath("$.city").value("CABA"))
                .andExpect(jsonPath("$.price").value(5000));
    }

    @Test
    void shouldCreateRestaurant() throws Exception {
        RestaurantRequestDto request = new RestaurantRequestDto(
                "Don Julio", "Calle 123", "CABA", new BigDecimal("5000")
        );

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Don Julio")
                .address("Calle 123")
                .city("CABA")
                .price(new BigDecimal("5000"))
                .build();

        when(restaurantService.create(any(RestaurantRequestDto.class))).thenReturn(restaurant);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/restaurants/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Don Julio"));
    }

    @Test
    void shouldUpdateRestaurant() throws Exception {
        RestaurantRequestDto request = new RestaurantRequestDto(
                "Nuevo", "Nueva 123", "La Plata", new BigDecimal("7000")
        );

        Restaurant updated = Restaurant.builder()
                .id(1L)
                .name("Nuevo")
                .address("Nueva 123")
                .city("La Plata")
                .price(new BigDecimal("7000"))
                .build();

        when(restaurantService.update(eq(1L), any(RestaurantRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/restaurants/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nuevo"))
                .andExpect(jsonPath("$.city").value("La Plata"));
    }

    @Test
    void shouldReturnAllRestaurants() throws Exception {
        when(restaurantService.findAll()).thenReturn(List.of(
                Restaurant.builder().id(1L).name("A").address("x").city("CABA").price(BigDecimal.ONE).build(),
                Restaurant.builder().id(2L).name("B").address("y").city("LP").price(BigDecimal.TEN).build()
        ));

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].name").value("B"));
    }
}