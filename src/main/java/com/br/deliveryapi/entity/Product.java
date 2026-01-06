package com.br.deliveryapi.entity;

import com.br.deliveryapi.dto.product.ProductDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean available;


    public ProductDto toDto() {
        return new ProductDto(
                this.id,
                this.name,
                this.description,
                this.price,
                this.available);
    }
}
