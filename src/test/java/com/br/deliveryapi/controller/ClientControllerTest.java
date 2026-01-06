package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.address.AddressDto;
import com.br.deliveryapi.dto.client.ClientDetailsDto;
import com.br.deliveryapi.dto.client.ClientUpdateDto;
import com.br.deliveryapi.entity.Client;
import com.br.deliveryapi.enums.Role;
import com.br.deliveryapi.enums.State;
import com.br.deliveryapi.security.CustomUserDetailsService;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.service.ClientService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientDetailsDto clientDetailsDto;
    private ClientUpdateDto clientUpdateDto;

    private String jwtToken;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        AddressDto addressDto = new AddressDto(
                1L,
                "Main St",
                100,
                "Downtown",
                "Springfield",
                State.SP,
                "12345-678"
        );

        clientDetailsDto = new ClientDetailsDto(
                1L,
                "John Doe",
                "john.doe@example.com",
                "11999999999",
                addressDto
        );

        clientUpdateDto = new ClientUpdateDto(
                "John Updated",
                "john.updated@example.com",
                "newpassword",
                "11888888888"
        );

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
    void getClientById_ShouldReturnClientDetails() throws Exception {
        when(clientService.findById(anyLong())).thenReturn(Mockito.mock(Client.class));
        when(clientService.findById(anyLong()).toDetailsDto()).thenReturn(clientDetailsDto);

        mockMvc.perform(get("/client/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientDetailsDto.id()))
                .andExpect(jsonPath("$.name").value(clientDetailsDto.name()))
                .andExpect(jsonPath("$.email").value(clientDetailsDto.email()))
                .andExpect(jsonPath("$.phone").value(clientDetailsDto.phone()))
                .andExpect(jsonPath("$.address.streetName").value(clientDetailsDto.address().streetName()));
    }

    @Test
    void updateClient_ShouldReturnUpdatedClient() throws Exception {
        Client mockedClient = mock(Client.class);

        when(clientService.update(eq(1L), any(Client.class))).thenReturn(mockedClient);
        when(mockedClient.toUpdateDto()).thenReturn(clientUpdateDto);

        mockMvc.perform(put("/client/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(clientUpdateDto.name()))
                .andExpect(jsonPath("$.email").value(clientUpdateDto.email()))
                .andExpect(jsonPath("$.phone").value(clientUpdateDto.phone()));
    }


    @Test
    void deleteClient_ShouldReturnNoContent() throws Exception {
        doNothing().when(clientService).deleteById(1L);

        mockMvc.perform(delete("/client/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        verify(clientService, times(1)).deleteById(1L);
    }
}

