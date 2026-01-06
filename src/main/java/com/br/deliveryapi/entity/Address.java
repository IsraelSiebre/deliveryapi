package com.br.deliveryapi.entity;

import com.br.deliveryapi.dto.address.AddressDto;
import com.br.deliveryapi.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "addresses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "street_name", "building_number", "district", "city", "state", "zip_code"
        })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String streetName;

    @Column(nullable = false)
    private int buildingNumber;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Pattern(regexp = "\\d{5}-\\d{3}")
    @Column(nullable = false)
    private String zipCode;

    public AddressDto toDto() {
        return new AddressDto(this.id,
                this.streetName,
                this.buildingNumber,
                this.district,
                this.city,
                this.state,
                this.zipCode);
    }

}
