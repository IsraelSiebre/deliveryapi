package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.admin.AdminRequestDto;
import com.br.deliveryapi.dto.admin.AdminResponseDto;
import com.br.deliveryapi.entity.Admin;
import com.br.deliveryapi.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/")
    public ResponseEntity<List<AdminResponseDto>> getAllAdmins() {
        List<AdminResponseDto> adminList = adminService.findAll()
                .stream()
                .map(Admin::toResponseDto)
                .toList();

        return ResponseEntity.ok(adminList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDto> getAdminById(@PathVariable Long id){
        return ResponseEntity.ok(adminService.findById(id).toResponseDto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDto> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminRequestDto dto) {
        return ResponseEntity.ok(adminService.update(id, dto.toEntity()).toResponseDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id){
        adminService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
