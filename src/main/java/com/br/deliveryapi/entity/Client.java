package com.br.deliveryapi.entity;

import com.br.deliveryapi.dto.client.ClientDetailsDto;
import com.br.deliveryapi.dto.client.ClientResponseDto;
import com.br.deliveryapi.dto.client.ClientUpdateDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    public ClientResponseDto toResponseDto() {
        return new ClientResponseDto(
                this.getId(),
                this.getName(),
                this.getEmail(),
                this.getPhone(),
                this.getAddress().toDto());
    }

    public ClientDetailsDto toDetailsDto() {
        return new ClientDetailsDto(
                this.getId(),
                this.getName(),
                this.getEmail(),
                this.getPhone(),
                this.getAddress().toDto());
    }

    public ClientUpdateDto toUpdateDto() {
        return new ClientUpdateDto(
                this.getName(),
                this.getEmail(),
                this.getPassword(),
                this.getPhone());
    }
}
