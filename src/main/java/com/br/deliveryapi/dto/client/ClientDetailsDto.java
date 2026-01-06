package com.br.deliveryapi.dto.client;

import com.br.deliveryapi.dto.address.AddressDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientDetailsDto(
        @NotBlank Long id,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotNull AddressDto address
) {}

