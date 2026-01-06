package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.product.ProductDto;
import com.br.deliveryapi.entity.Product;
import com.br.deliveryapi.enums.Role;
import com.br.deliveryapi.security.CustomUserDetailsService;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.service.ProductService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private ProductDto productDto;


    private String jwtToken;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        product = new Product(
                1L,
                "Pizza",
                "Delicious pizza",
                new BigDecimal("49.90"),
                true);

        productDto = new ProductDto(
                1L,
                "Pizza",
                "Delicious pizza",
                new BigDecimal("49.90"),
                true);

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
    void shouldCreateProduct() throws Exception {
        Mockito.when(productService.create(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(jsonPath("$.description").value("Delicious pizza"))
                .andExpect(jsonPath("$.price").value(49.90))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Mockito.when(productService.findAll()).thenReturn(List.of(product));

        mockMvc.perform(get("/product/")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    void shouldGetProductById() throws Exception {
        Mockito.when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/product/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Mockito.when(productService.update(eq(1L), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).deleteById(1L);

        mockMvc.perform(delete("/product/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}

