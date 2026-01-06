package com.br.deliveryapi.service;

import com.br.deliveryapi.entity.Address;
import com.br.deliveryapi.exception.ResourceNotFoundException;
import com.br.deliveryapi.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    AddressRepository addressRepository;

    public Address create(Address address) {
        return addressRepository.findByStreetNameAndBuildingNumberAndDistrictAndCityAndStateAndZipCode(
                address.getStreetName(),
                address.getBuildingNumber(),
                address.getDistrict(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        ).orElseGet(() -> addressRepository.save(address));
    }


    public Address findById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address Not Found"));
    }

    public Address update(Long id, Address newAddress) {
        Address oldAddress = this.findById(id);

        oldAddress.setStreetName(newAddress.getStreetName());
        oldAddress.setBuildingNumber(newAddress.getBuildingNumber());
        oldAddress.setDistrict(newAddress.getDistrict());
        oldAddress.setCity(newAddress.getCity());
        oldAddress.setState(newAddress.getState());
        oldAddress.setZipCode(newAddress.getZipCode());

        return addressRepository.findByStreetNameAndBuildingNumberAndDistrictAndCityAndStateAndZipCode(
                        oldAddress.getStreetName(),
                        oldAddress.getBuildingNumber(),
                        oldAddress.getDistrict(),
                        oldAddress.getCity(),
                        oldAddress.getState(),
                        oldAddress.getZipCode()
                )
                .filter(existingAddress -> !existingAddress.getId().equals(oldAddress.getId()))
                .orElseGet(() -> addressRepository.save(oldAddress));
    }

    public void deleteById(Long id) {
        Address address = this.findById(id);
        addressRepository.delete(address);
    }
}
