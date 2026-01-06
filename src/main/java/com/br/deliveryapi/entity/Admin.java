package com.br.deliveryapi.entity;

import com.br.deliveryapi.dto.admin.AdminResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Admin extends User {

    public AdminResponseDto toResponseDto() {
        return new AdminResponseDto(
                this.getId(),
                this.getName(),
                this.getEmail(),
                this.getPhone());
    }

}

