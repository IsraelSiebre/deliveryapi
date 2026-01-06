package com.br.deliveryapi.dto.product;

import com.br.deliveryapi.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        @NotBlank String name,
        @NotBlank String description,
        @NotNull BigDecimal price,
        @NotNull Boolean available

        ) {

    public Product toEntity() {
        return new Product(
          id,
          name,
          description,
          price,
          available
        );
    }

}
