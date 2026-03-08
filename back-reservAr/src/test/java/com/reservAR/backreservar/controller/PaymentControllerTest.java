package com.reservAR.backreservar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservAR.backreservar.jwt.JwtFilter;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.service.CustomUserDetailsService;
import com.reservAR.backreservar.service.IPaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPaymentService paymentService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailService;

    @Test
    void shouldCreateMercadoPagoPayment() throws Exception {
        when(paymentService.paymentReservation(1L)).thenReturn("https://mp-link.test");

        mockMvc.perform(post("/payment/mercado-pago/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("https://mp-link.test"));
    }

    @Test
    void shouldProcessWebhook() throws Exception {
        Map<String, Object> body = Map.of("type", "payment");

        doNothing().when(paymentService).processPayment(anyMap(), anyMap(), anyMap());

        mockMvc.perform(post("/payment/mercado-pago/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("x-signature", "abc")
                        .param("data.id", "123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));
    }
}