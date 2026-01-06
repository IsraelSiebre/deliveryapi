package com.br.deliveryapi.entity;

import com.br.deliveryapi.dto.order.OrderItemDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    public OrderItem(Long id, Order order, Product product, Integer quantity) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = this.product.getPrice().multiply(new BigDecimal(this.quantity));
    }

    public OrderItemDto toDto() {
        return new OrderItemDto(
                this.id,
                this.order,
                this.product,
                this.quantity,
                this.totalPrice
        );
    }

    public void calculateTotalPrice() {
        this.totalPrice = this.getProduct().getPrice().multiply(new BigDecimal(this.quantity));
    }
}
