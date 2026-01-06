package com.br.deliveryapi.repository;

import com.br.deliveryapi.entity.Address;
import com.br.deliveryapi.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByStreetNameAndBuildingNumberAndDistrictAndCityAndStateAndZipCode(
            String streetName,
            int buildingNumber,
            String district,
            String city,
            State state,
            String zipCode);
}
