package com.br.deliveryapi.dto.order;

import com.br.deliveryapi.entity.Order;
import com.br.deliveryapi.entity.OrderItem;
import com.br.deliveryapi.entity.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemDto (

        Long id,
        @NotNull Order order,
        @NotNull Product product,
        @NotNull @Positive Integer quantity,
        BigDecimal totalPrice

        ) {

    public OrderItem toEntity() {
        return new OrderItem(
                this.id(),
                this.order(),
                this.product(),
                this.quantity());
    }

}
