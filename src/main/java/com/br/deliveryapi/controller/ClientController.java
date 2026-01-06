package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.client.ClientDetailsDto;
import com.br.deliveryapi.dto.client.ClientUpdateDto;
import com.br.deliveryapi.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ClientDetailsDto> getClientById(@PathVariable Long id){
        return ResponseEntity.ok(clientService.findById(id).toDetailsDto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientUpdateDto> updateClient(@PathVariable Long id,
                                                             @Valid @RequestBody ClientUpdateDto dto) {
        return ResponseEntity.ok(clientService.update(id, dto.toEntity()).toUpdateDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id){
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
