package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.LoginResponse;
import com.reservAR.backreservar.dto.UserLoginDto;
import com.reservAR.backreservar.dto.UserRegisterDto;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.Role;
import com.reservAR.backreservar.model.User;
import com.reservAR.backreservar.repository.UserRepository;
import com.reservAR.backreservar.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterCustomer() {
        UserRegisterDto dto = new UserRegisterDto(
                "test@mail.com", "1234", "Juan", "Lopez", "111111"
        );

        when(passwordEncoder.encode("1234")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.register(dto);

        assertEquals(Role.CUSTOMER, result.getRole());
        assertEquals("encoded-pass", result.getPassword());
    }

    @Test
    void shouldRegisterStaff() {
        UserRegisterDto dto = new UserRegisterDto(
                "staff@mail.com", "1234", "Ana", "Lopez", "222222"
        );

        when(passwordEncoder.encode("1234")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerStaff(dto);

        assertEquals(Role.STAFF, result.getRole());
        assertEquals("encoded-pass", result.getPassword());
    }

    @Test
    void shouldLoginAndReturnToken() {
        UserLoginDto dto = new UserLoginDto("test@mail.com", "1234");
        Authentication auth = new TestingAuthenticationToken("test@mail.com", "1234");
        auth.setAuthenticated(true);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken("test@mail.com")).thenReturn("jwt-token");

        LoginResponse response = userService.login(dto);

        assertEquals("jwt-token", response.token());
    }

    @Test
    void shouldThrowWhenAuthenticationFails() {
        UserLoginDto dto = new UserLoginDto("test@mail.com", "1234");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
    }
}