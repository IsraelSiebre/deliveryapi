package com.br.deliveryapi.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminResponseDto (
            @NotBlank Long id,
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotBlank String phone
    ) {}

