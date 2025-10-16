package com.reservAR.backreservar.dto;

import jakarta.validation.constraints.NotNull;

public record UserRegisterDto(
        @NotNull String email,
        @NotNull String password,
        @NotNull String name,
        @NotNull String surname,
        @NotNull String phone
) {
}
