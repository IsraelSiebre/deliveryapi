package com.br.deliveryapi.dto.address;

import com.br.deliveryapi.entity.Address;
import com.br.deliveryapi.enums.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressDto(
        Long id,
        @NotBlank String streetName,
        @NotNull Integer buildingNumber,
        @NotBlank String district,
        @NotBlank String city,
        @NotNull State state,
        @NotBlank String zipCode

) {

    public Address toEntity() {
        return new Address(id,
                streetName,
                buildingNumber,
                district,
                city,
                state,
                zipCode);
    }

}

