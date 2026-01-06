package com.br.deliveryapi.dto.order;

import com.br.deliveryapi.entity.Client;
import com.br.deliveryapi.entity.Order;
import com.br.deliveryapi.entity.OrderItem;
import com.br.deliveryapi.enums.DeliveryOption;
import com.br.deliveryapi.enums.OrderStatus;
import com.br.deliveryapi.enums.PayMethod;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record OrderDto (

        Long id,
        LocalDateTime dateTime,
        OrderStatus orderStatus,
        @NotNull PayMethod payMethod,
        BigDecimal price,
        @NotNull DeliveryOption deliveryOption,
        List<OrderItemDto> items,
        @NotNull Client client

) {

    private List<OrderItem> toEntityList(List<OrderItemDto> dtoList) {
        if (dtoList == null) return new ArrayList<>();
        return dtoList.stream().map(OrderItemDto::toEntity).toList();
    }


    public Order toEntity() {
        return new Order(
                id,
                dateTime,
                orderStatus,
                payMethod,
                price,
                deliveryOption,
                toEntityList(items),
                client
        );
    }

}

