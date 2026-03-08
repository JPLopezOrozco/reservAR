package com.reservAR.backreservar.service;

import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.CustomUserDetails;
import com.reservAR.backreservar.model.Role;
import com.reservAR.backreservar.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "JB46vDu8MzAJh0s+HEmj2EtegRs/EJJ1i9EVP48j6Hc=");
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtService.generateToken("user@mail.com");

        User user = User.builder()
                .email("user@mail.com")
                .role(Role.CUSTOMER)
                .password("encoded")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertNotNull(token);
        assertEquals("user@mail.com", jwtService.extractUsername(token));
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        String token = jwtService.generateToken("user@mail.com");

        User user = User.builder()
                .email("other@mail.com")
                .role(Role.CUSTOMER)
                .password("encoded")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertFalse(jwtService.validateToken(token, userDetails));
    }
}