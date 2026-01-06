package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.address.AddressDto;
import com.br.deliveryapi.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long id){
        return ResponseEntity.ok(addressService.findById(id).toDto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(addressService.update(id, addressDto.toEntity()).toDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id){
        addressService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
