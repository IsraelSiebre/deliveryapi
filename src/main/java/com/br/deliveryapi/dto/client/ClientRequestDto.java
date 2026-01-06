package com.br.deliveryapi.dto.client;

import com.br.deliveryapi.dto.address.AddressDto;
import com.br.deliveryapi.entity.Client;
import com.br.deliveryapi.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientRequestDto(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String phone,
        @Valid @NotNull AddressDto address
) {
    public Client toEntity() {
        return Client.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .phone(this.phone)
                .address(this.address.toEntity())
                .role(Role.CLIENT)
                        .build();
    }
}
