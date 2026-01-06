package com.br.deliveryapi.dto.admin;

import com.br.deliveryapi.entity.Admin;
import com.br.deliveryapi.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminRequestDto(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String phone
) {
    public Admin toEntity() {
        return Admin.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .phone(this.phone)
                .role(Role.ADMIN)
                .build();
    }
}

