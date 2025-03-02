package com.giarts.ateliegiarts.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull(message = "Username is required")
        @Email(message = "Email must be valid")
        String username,

        @NotNull(message = "Password is required")
        String password
) {
}
