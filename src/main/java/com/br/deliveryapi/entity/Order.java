package com.br.deliveryapi.entity;

import com.br.deliveryapi.dto.order.OrderDto;
import com.br.deliveryapi.dto.order.OrderItemDto;
import com.br.deliveryapi.enums.DeliveryOption;
import com.br.deliveryapi.enums.OrderStatus;
import com.br.deliveryapi.enums.PayMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayMethod payMethod;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryOption deliveryOption;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    private List<OrderItemDto> toDtoList(List<OrderItem> entityList) {
        ArrayList<OrderItemDto> dtoList = new ArrayList<>();

        for (OrderItem entity : entityList) {
            dtoList.add(entity.toDto());
        }

        return dtoList.stream().toList();
    }

    public OrderDto toDto() {
        return new OrderDto(
                this.id,
                this.createdAt,
                this.orderStatus,
                this.payMethod,
                this.price,
                this.deliveryOption,
                toDtoList(this.items),
                this.client
        );
    }
}
