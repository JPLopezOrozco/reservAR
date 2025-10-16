package com.reservAR.backreservar.service.impl;

import com.reservAR.backreservar.dto.LoginResponse;
import com.reservAR.backreservar.dto.UserLoginDto;
import com.reservAR.backreservar.dto.UserRegisterDto;
import com.reservAR.backreservar.exception.UserNotFoundException;
import com.reservAR.backreservar.jwt.JwtService;
import com.reservAR.backreservar.model.Role;
import com.reservAR.backreservar.model.User;
import com.reservAR.backreservar.repository.UserRepository;
import com.reservAR.backreservar.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User register(UserRegisterDto user) {
        User newUser = User.builder()
                .email(user.email())
                .password(passwordEncoder.encode(user.password()))
                .name(user.name())
                .surname(user.surname())
                .phone(user.phone())
                .role(Role.CUSTOMER)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public User registerStaff(UserRegisterDto user) {
        User newStaff = User.builder()
                .email(user.email())
                .password(passwordEncoder.encode(user.password()))
                .name(user.name())
                .surname(user.surname())
                .phone(user.phone())
                .role(Role.STAFF)
                .build();
        return userRepository.save(newStaff);
    }

    @Override
    @Transactional
    public LoginResponse login(UserLoginDto user) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
            if (authentication.isAuthenticated()) {
                return new LoginResponse(
                        jwtService.generateToken(user.email())
                );
            }else {
                throw new BadCredentialsException("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Authentication failed", e);
        }
    }
}
