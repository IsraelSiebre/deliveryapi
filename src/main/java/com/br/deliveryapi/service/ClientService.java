package com.br.deliveryapi.service;

import com.br.deliveryapi.entity.Address;
import com.br.deliveryapi.entity.Client;
import com.br.deliveryapi.exception.UserAlreadyExistsException;
import com.br.deliveryapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final AddressService addressService;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, AddressService addressService, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
    }

    public Client create(Client client) {
         if (clientRepository.existsByEmail(client.getEmail())) {
            throw new UserAlreadyExistsException("Client Already Exists");
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));

        Address savedAddress = addressService.create(client.getAddress());
        client.setAddress(savedAddress);

        return clientRepository.save(client);
    }
    

    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client Not Found!"));
    }

    public Client update(Long id, Client newClient) {
        Client oldClient = this.findById(id);

        if (clientRepository.existsByEmail(newClient.getEmail())) {
            throw new UserAlreadyExistsException("Client Already Exists");
        }

        Address savedAddress = addressService.create(newClient.getAddress());
        oldClient.setAddress(savedAddress);

        oldClient.setEmail(newClient.getEmail());
        oldClient.setName(newClient.getName());
        oldClient.setPassword(passwordEncoder.encode(newClient.getPassword()));
        oldClient.setPhone(newClient.getPhone());

        return clientRepository.save(oldClient);
    }

    public void deleteById(Long id) {
        Client client = this.findById(id);
        clientRepository.delete(client);
    }
}
