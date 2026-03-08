package com.reservAR.backreservar.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservAR.backreservar.dto.TableRequestDto;
import com.reservAR.backreservar.jwt.JwtFilter;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.model.TableEntity;
import com.reservAR.backreservar.service.CustomUserDetailsService;
import com.reservAR.backreservar.service.impl.TableEntityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableEntityController.class)
@AutoConfigureMockMvc(addFilters = false)
class TableEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TableEntityService tableEntityService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailService;

    @Test
    void shouldReturnTableById() throws Exception {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();
        TableEntity table = TableEntity.builder()
                .id(1L)
                .restaurant(restaurant)
                .code("M1")
                .minCapacity(2)
                .maxCapacity(4)
                .build();

        when(tableEntityService.findById(1L)).thenReturn(table);

        mockMvc.perform(get("/table/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.code").value("M1"))
                .andExpect(jsonPath("$.minCapacity").value(2))
                .andExpect(jsonPath("$.maxCapacity").value(4));
    }

    @Test
    void shouldCreateTable() throws Exception {
        TableRequestDto request = new TableRequestDto(1L, "M1", 2, 4);

        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();
        TableEntity table = TableEntity.builder()
                .id(10L)
                .restaurant(restaurant)
                .code("M1")
                .minCapacity(2)
                .maxCapacity(4)
                .build();

        when(tableEntityService.save(any(TableRequestDto.class))).thenReturn(table);

        mockMvc.perform(post("/table")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/table/10"))
                .andExpect(jsonPath("$.code").value("M1"));
    }

    @Test
    void shouldReturnTablesByRestaurant() throws Exception {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();

        when(tableEntityService.findByRestaurantId(1L)).thenReturn(List.of(
                TableEntity.builder().id(1L).restaurant(restaurant).code("M1").minCapacity(2).maxCapacity(4).build(),
                TableEntity.builder().id(2L).restaurant(restaurant).code("M2").minCapacity(4).maxCapacity(6).build()
        ));

        mockMvc.perform(get("/table/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("M1"))
                .andExpect(jsonPath("$[1].code").value("M2"));
    }

    @Test
    void shouldDeleteTable() throws Exception {
        doNothing().when(tableEntityService).deleteById(1L);

        mockMvc.perform(delete("/table/delete/1"))
                .andExpect(status().isNoContent());
    }
}