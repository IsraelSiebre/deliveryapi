package com.br.deliveryapi.dto.user;

import com.br.deliveryapi.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDto(
        Long id,
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String phone,
        Role role
) { }
