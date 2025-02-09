package com.giarts.ateliegiarts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.giarts.ateliegiarts.enums.EUserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull
    @NotBlank
    private String name;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private EUserRole userRole;
}
