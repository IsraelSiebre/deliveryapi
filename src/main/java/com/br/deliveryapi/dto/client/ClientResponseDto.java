package com.br.deliveryapi.dto.client;

import com.br.deliveryapi.dto.address.AddressDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientResponseDto(
        @NotBlank Long id,
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String phone,
        @NotBlank AddressDto addressDto
) {}

