package com.br.deliveryapi.dto.client;

import com.br.deliveryapi.entity.Client;
import com.br.deliveryapi.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientUpdateDto(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String phone
) {
    public Client toEntity() {
        return Client.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .phone(this.phone)
                .role(Role.CLIENT)
                .build();
    }
}
