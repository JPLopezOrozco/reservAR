package com.reservAR.backreservar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservAR.backreservar.dto.InventoryRuleRequestDto;
import com.reservAR.backreservar.jwt.JwtFilter;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.InventoryRule;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.service.CustomUserDetailsService;
import com.reservAR.backreservar.service.IInventoryRuleService;
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

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IInventoryRuleService inventoryRuleService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailService;

    @Test
    void shouldReturnRuleById() throws Exception {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();

        InventoryRule rule = InventoryRule.builder()
                .id(1L)
                .restaurant(restaurant)
                .defaultDurationMin(90)
                .prepBufferMin(15)
                .cleanupBufferMin(15)
                .gracePeriodMin(10)
                .build();

        when(inventoryRuleService.findById(1L)).thenReturn(rule);

        mockMvc.perform(get("/rule/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.defaultDurationMin").value(90));
    }

    @Test
    void shouldCreateRule() throws Exception {
        InventoryRuleRequestDto request = new InventoryRuleRequestDto(1L, 90, 15, 15, 10);

        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();
        InventoryRule rule = InventoryRule.builder()
                .id(7L)
                .restaurant(restaurant)
                .defaultDurationMin(90)
                .prepBufferMin(15)
                .cleanupBufferMin(15)
                .gracePeriodMin(10)
                .build();

        when(inventoryRuleService.save(any(InventoryRuleRequestDto.class))).thenReturn(rule);

        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/rule/7"))
                .andExpect(jsonPath("$.restaurantName").value("Don Julio"));
    }

    @Test
    void shouldReturnAllRules() throws Exception {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();

        when(inventoryRuleService.findAll()).thenReturn(List.of(
                InventoryRule.builder().id(1L).restaurant(restaurant).defaultDurationMin(90).prepBufferMin(15).cleanupBufferMin(15).gracePeriodMin(10).build()
        ));

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].restaurantName").value("Don Julio"));
    }

    @Test
    void shouldDeleteRule() throws Exception {
        doNothing().when(inventoryRuleService).deleteById(1L);

        mockMvc.perform(delete("/rule/delete/1"))
                .andExpect(status().isNoContent());
    }
}