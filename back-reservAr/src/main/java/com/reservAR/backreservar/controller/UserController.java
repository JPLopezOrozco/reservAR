package com.reservAR.backreservar.controller;

import com.reservAR.backreservar.dto.LoginResponse;
import com.reservAR.backreservar.dto.UserLoginDto;
import com.reservAR.backreservar.dto.UserRegisterDto;
import com.reservAR.backreservar.dto.UserResponseDto;
import com.reservAR.backreservar.model.User;
import com.reservAR.backreservar.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;


    @PostMapping("/user")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRegisterDto userRequestDto){
        User user = userService.register(userRequestDto);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(location).body(UserResponseDto.of(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/staff")
    public ResponseEntity<UserResponseDto> registerStaff(@RequestBody @Valid UserRegisterDto userRequestDto){
        User user = userService.registerStaff(userRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(UserResponseDto.of(user));
    }


    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid UserLoginDto userLoginDto){
        return ResponseEntity.ok(userService.login(userLoginDto));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(UserResponseDto.of(userService.findById(id)));
    }
}
