package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.admin.AdminRequestDto;
import com.br.deliveryapi.dto.admin.AdminResponseDto;
import com.br.deliveryapi.entity.Admin;
import com.br.deliveryapi.enums.Role;
import com.br.deliveryapi.security.CustomUserDetailsService;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.security.TokenBlacklistService;
import com.br.deliveryapi.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private Admin sampleAdmin;
    private AdminRequestDto sampleAdminRequestDto;
    private AdminResponseDto sampleAdminResponseDto;

    private String jwtToken;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        sampleAdmin = Admin.builder()
                .id(1L)
                .name("Admin Test")
                .email("admin@test.com")
                .password("123456")
                .phone("11999999999")
                .role(Role.ADMIN)
                .build();

        sampleAdminResponseDto = sampleAdmin.toResponseDto();

        sampleAdminRequestDto = new AdminRequestDto(
                "Admin Test",
                "admin@test.com",
                "123456",
                "11999999999"
        );

        userDetails = User.builder()
                .username("admin@test.com")
                .password("123456")
                .roles(Role.ADMIN.toString())
                .build();

        jwtToken = "mocked-jwt-token";

        when(jwtUtil.getEmailFromToken(jwtToken)).thenReturn("admin@test.com");
        when(jwtUtil.validateToken(jwtToken, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
    }


    @Test
    void testGetAllAdmins() throws Exception {
        when(adminService.findAll()).thenReturn(List.of(sampleAdmin));

        mockMvc.perform(get("/admin/")
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleAdmin.getId()))
                .andExpect(jsonPath("$[0].name").value(sampleAdmin.getName()))
                .andExpect(jsonPath("$[0].email").value(sampleAdmin.getEmail()))
                .andExpect(jsonPath("$[0].phone").value(sampleAdmin.getPhone()));
    }

    @Test
    void testGetAdminById() throws Exception {
        when(adminService.findById(1L)).thenReturn(sampleAdmin);

        mockMvc.perform(get("/admin/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleAdmin.getId()))
                .andExpect(jsonPath("$.name").value(sampleAdmin.getName()))
                .andExpect(jsonPath("$.email").value(sampleAdmin.getEmail()))
                .andExpect(jsonPath("$.phone").value(sampleAdmin.getPhone()));
    }

    @Test
    void testUpdateAdmin() throws Exception {
        when(adminService.update(eq(1L), any(Admin.class))).thenReturn(sampleAdmin);

        mockMvc.perform(put("/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAdminRequestDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleAdmin.getId()))
                .andExpect(jsonPath("$.name").value(sampleAdmin.getName()))
                .andExpect(jsonPath("$.email").value(sampleAdmin.getEmail()));
    }

    @Test
    void testDeleteAdmin() throws Exception {
        Mockito.doNothing().when(adminService).deleteById(1L);

        mockMvc.perform(delete("/admin/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}

