package com.reservAR.backreservar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservAR.backreservar.dto.LoginResponse;
import com.reservAR.backreservar.dto.UserLoginDto;
import com.reservAR.backreservar.dto.UserRegisterDto;
import com.reservAR.backreservar.jwt.JwtFilter;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.Role;
import com.reservAR.backreservar.model.User;
import com.reservAR.backreservar.service.CustomUserDetailsService;
import com.reservAR.backreservar.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailService;

    @Test
    void shouldRegisterUser() throws Exception {
        UserRegisterDto request = new UserRegisterDto(
                "juan@mail.com", "1234", "Juan", "Lopez", "11223344"
        );

        User user = User.builder()
                .id(1L)
                .email("juan@mail.com")
                .name("Juan")
                .surname("Lopez")
                .phone("11223344")
                .role(Role.CUSTOMER)
                .build();

        when(userService.register(any(UserRegisterDto.class))).thenReturn(user);

        mockMvc.perform(post("/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/auth/user/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void shouldLogin() throws Exception {
        UserLoginDto request = new UserLoginDto("juan@mail.com", "1234");

        when(userService.login(any(UserLoginDto.class))).thenReturn(new LoginResponse("jwt-token"));

        mockMvc.perform(post("/auth/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("juan@mail.com")
                .name("Juan")
                .surname("Lopez")
                .phone("11223344")
                .role(Role.CUSTOMER)
                .build();

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/auth/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.name").value("Juan"));
    }
}
