package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.address.AddressDto;
import com.br.deliveryapi.dto.admin.AdminRequestDto;
import com.br.deliveryapi.dto.client.ClientResponseDto;
import com.br.deliveryapi.dto.client.ClientRequestDto;
import com.br.deliveryapi.dto.user.LoginRequestDTO;
import com.br.deliveryapi.enums.Role;
import com.br.deliveryapi.enums.State;
import com.br.deliveryapi.security.CustomUserDetailsService;
import com.br.deliveryapi.service.AdminService;
import com.br.deliveryapi.service.ClientService;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.security.TokenBlacklistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDTO validLoginRequest;
    private ClientRequestDto clientRequestDto;
    private AdminRequestDto adminRequestDto;
    private AddressDto addressDto;

    private String jwtToken;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        validLoginRequest = new LoginRequestDTO("client@test.com", "123456");

        clientRequestDto = new ClientRequestDto(
                "Client Name",
                "client@test.com",
                "123456",
                "11999999999",
                new AddressDto(
                        null,
                        "Rua dos Pinheiros",
                        77,
                        "Savassi",
                        "Belo Horizonte",
                        State.MG,
                        "30130-000"
                )
        );

        adminRequestDto = new AdminRequestDto(
                "Admin Name",
                "admin@test.com",
                "123456",
                "11999999999"
        );

        addressDto = new AddressDto(
                null,
                "Rua dos Pinheiros",
                77,
                "Savassi",
                "Belo Horizonte",
                State.MG,
                "30130-000"
        );

        userDetails = User.builder()
                .username("admin@test.com")
                .password("123456")
                .roles(Role.ADMIN.toString())
                .build();

        jwtToken = "mocked-jwt-token";

        when(jwtUtil.getEmailFromToken(jwtToken)).thenReturn("admin@test.com");
        when(jwtUtil.validateToken(jwtToken, userDetails)).thenReturn(true);
        when(tokenBlacklistService.isTokenBlacklisted(jwtToken)).thenReturn(false);
        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);

    }



    @Test
    void registerClient_Success() throws Exception {
        ClientResponseDto responseDto = new ClientResponseDto(
                1L,
                clientRequestDto.name(),
                clientRequestDto.email(),
                clientRequestDto.password(),
                addressDto);

        when(clientService.create(any())).thenReturn(clientRequestDto.toEntity());
        // Mock the toResponseDto() method behavior indirectly by returning a ClientResponseDto from service (for simplicity, assume entity conversion is correct)

        mockMvc.perform(post("/auth/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientRequestDto)))
                .andExpect(status().isCreated());
    }


    @Test
    void registerAdmin_Success() throws Exception {
        when(adminService.create(any())).thenReturn(adminRequestDto.toEntity());

        mockMvc.perform(post("/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_Success() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(jwtUtil.generateToken("client@test.com")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void login_Failure() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void logout_Success() throws Exception {
        String token = "Bearer mocked-jwt-token";

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful. Token blacklisted."));

        verify(tokenBlacklistService, times(1)).add("mocked-jwt-token");
    }

}

