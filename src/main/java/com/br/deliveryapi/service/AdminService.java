package com.br.deliveryapi.service;

import com.br.deliveryapi.entity.Admin;
import com.br.deliveryapi.exception.ResourceNotFoundException;
import com.br.deliveryapi.exception.UserAlreadyExistsException;
import com.br.deliveryapi.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin create(Admin admin) {
         if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new UserAlreadyExistsException("Admin Already Exists");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        return adminRepository.save(admin);
    }

    public Admin update(Long id, Admin newAdmin) {
        Admin oldAdmin = this.findById(id);

        if (adminRepository.existsByEmail(newAdmin.getEmail())) {
            throw new UserAlreadyExistsException("Admin Already Exists");
        }

        oldAdmin.setName(newAdmin.getName());
        oldAdmin.setEmail(newAdmin.getEmail());
        oldAdmin.setPhone(newAdmin.getPhone());
        oldAdmin.setPassword(passwordEncoder.encode(newAdmin.getPassword()));

        return adminRepository.save(oldAdmin);
    }


    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin Not Found"));
    }

    public void deleteById(Long id) {
        Admin admin = this.findById(id);
        adminRepository.delete(admin);
    }
}

