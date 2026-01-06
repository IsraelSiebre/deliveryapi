package com.br.deliveryapi.repository;

import com.br.deliveryapi.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
    boolean existsByEmail(String email);
}
