package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.address.AddressDto;
import com.br.deliveryapi.entity.Address;
import com.br.deliveryapi.enums.Role;
import com.br.deliveryapi.enums.State;
import com.br.deliveryapi.security.CustomUserDetailsService;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.service.AddressService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private Address sampleAddress;
    private AddressDto sampleAddressDto;

    private String jwtToken;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        sampleAddress = new Address(
                1L,
                "Rua das Flores",
                123,
                "Centro",
                "São Paulo",
                State.SP,
                "12345-678"
        );

        sampleAddressDto = sampleAddress.toDto();

        userDetails = User.builder()
                .username("john.doe@example.com")
                .password("123456")
                .roles(Role.CLIENT.toString())
                .build();

        jwtToken = "mocked-jwt-token";

        when(jwtUtil.getEmailFromToken(jwtToken)).thenReturn("john.doe@example.com");
        when(jwtUtil.validateToken(jwtToken, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(userDetails);
    }

    @Test
    void testGetAddressById() throws Exception {
        Mockito.when(addressService.findById(1L)).thenReturn(sampleAddress);

        mockMvc.perform(get("/address/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleAddress.getId()))
                .andExpect(jsonPath("$.streetName").value(sampleAddress.getStreetName()))
                .andExpect(jsonPath("$.buildingNumber").value(sampleAddress.getBuildingNumber()))
                .andExpect(jsonPath("$.district").value(sampleAddress.getDistrict()))
                .andExpect(jsonPath("$.city").value(sampleAddress.getCity()))
                .andExpect(jsonPath("$.state").value(sampleAddress.getState().name()))
                .andExpect(jsonPath("$.zipCode").value(sampleAddress.getZipCode()));
    }

    @Test
    void testUpdateAddress() throws Exception {
        Mockito.when(addressService.update(eq(1L), any(Address.class))).thenReturn(sampleAddress);

        mockMvc.perform(put("/address/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAddressDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleAddress.getId()))
                .andExpect(jsonPath("$.streetName").value(sampleAddress.getStreetName()));
    }

    @Test
    void testDeleteAddress() throws Exception {
        Mockito.doNothing().when(addressService).deleteById(1L);

        mockMvc.perform(delete("/address/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}

