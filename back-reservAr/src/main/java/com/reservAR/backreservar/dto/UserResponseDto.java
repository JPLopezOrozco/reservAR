package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.Role;
import com.reservAR.backreservar.model.User;

public record UserResponseDto (
        Long id,
        String email,
        String name,
        String surname,
        String phone,
        Role role
){
    public static UserResponseDto of(User user){
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getRole()
        );
    }
}
