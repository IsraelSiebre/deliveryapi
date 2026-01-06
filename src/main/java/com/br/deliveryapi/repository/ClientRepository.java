package com.br.deliveryapi.repository;

import com.br.deliveryapi.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByEmail(String email);
    boolean existsByEmail(String email);
}
