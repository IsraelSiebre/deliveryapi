package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.order.OrderDto;
import com.br.deliveryapi.dto.order.OrderItemDto;
import com.br.deliveryapi.dto.order.PeriodRequestDto;
import com.br.deliveryapi.entity.Client;
import com.br.deliveryapi.entity.Order;
import com.br.deliveryapi.enums.DeliveryOption;
import com.br.deliveryapi.enums.OrderStatus;
import com.br.deliveryapi.enums.PayMethod;
import com.br.deliveryapi.enums.Role;
import com.br.deliveryapi.security.CustomUserDetailsService;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDto exampleOrderDto;

    private String jwtToken;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        Client client = new Client();
        client.setId(1L);
        client.setName("John Doe");
        client.setEmail("john@example.com");
        client.setPhone("11999999999");

        exampleOrderDto = new OrderDto(
                1L,
                LocalDateTime.now(),
                OrderStatus.PENDING,
                PayMethod.PIX,
                new BigDecimal("100.00"),
                DeliveryOption.HOME_DELIVERY,
                List.of(),
                client
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
    void createOrder_ReturnsCreatedOrder() throws Exception {
        when(orderService.create(any(Order.class))).thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(post("/orders/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exampleOrderDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(exampleOrderDto.id()))
                .andExpect(jsonPath("$.orderStatus").value(exampleOrderDto.orderStatus().name()))
                .andExpect(jsonPath("$.payMethod").value(exampleOrderDto.payMethod().name()));
    }

    @Test
    void getAllOrders_ReturnsList() throws Exception {
        when(orderService.findAll()).thenReturn(List.of(exampleOrderDto.toEntity()));

        mockMvc.perform(get("/orders/")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(exampleOrderDto.id()));
    }

    @Test
    void getOrderById_ReturnsOrder() throws Exception {
        when(orderService.findById(anyLong())).thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(get("/orders/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exampleOrderDto.id()));
    }

    @Test
    void updateOrder_ReturnsUpdatedOrder() throws Exception {
        when(orderService.update(eq(1L), any(Order.class))).thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(put("/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exampleOrderDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exampleOrderDto.id()));
    }

    @Test
    void updateOrderStatus_ReturnsUpdatedOrder() throws Exception {
        UserDetails adminUser = User.builder()
                .username("admin@example.com")
                .password("123456")
                .roles(Role.ADMIN.toString())
                .build();

        when(jwtUtil.getEmailFromToken(jwtToken)).thenReturn("admin@example.com");
        when(jwtUtil.validateToken(jwtToken, adminUser)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin@example.com")).thenReturn(adminUser);

        when(orderService.updateStatus(eq(1L), eq(OrderStatus.PREPARING)))
                .thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(patch("/orders/{id}/{status}", 1L, OrderStatus.PREPARING)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.PENDING.name()));
    }



    @Test
    void deleteOrder_ReturnsNoContent() throws Exception {
        doNothing().when(orderService).deleteById(1L);

        mockMvc.perform(delete("/orders/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteById(1L);
    }

    @Test
    void getOrdersByClientId_ReturnsList() throws Exception {
        when(orderService.findAllByClientId(anyLong())).thenReturn(List.of(exampleOrderDto.toEntity()));

        mockMvc.perform(get("/orders/client/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(exampleOrderDto.id()));
    }

    @Test
    void getOrdersByStatus_ReturnsList() throws Exception {
        when(orderService.findAllByOrderStatus(any(OrderStatus.class))).thenReturn(List.of(exampleOrderDto.toEntity()));

        mockMvc.perform(get("/orders/status/{status}", OrderStatus.PENDING)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value(OrderStatus.PENDING.name()));
    }

    @Test
    void getOrdersByPeriod_ReturnsList() throws Exception {
        PeriodRequestDto period = new PeriodRequestDto(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        when(orderService.findByPeriod(any(), any())).thenReturn(List.of(exampleOrderDto.toEntity()));

        mockMvc.perform(post("/orders/period")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(period))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(exampleOrderDto.id()));
    }

    @Test
    void addItemToOrder_ReturnsOrder() throws Exception {
        OrderItemDto orderItemDto = new OrderItemDto(null, null, null, 1, null);
        when(orderService.addItem(eq(1L), any(OrderItemDto.class))).thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(post("/orders/{orderId}/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderItemDto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exampleOrderDto.id()));
    }

    @Test
    void removeItemFromOrder_ReturnsOrder() throws Exception {
        when(orderService.removeItem(1L, 1L)).thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(delete("/orders/{orderId}/items/{itemId}", 1L, 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exampleOrderDto.id()));
    }

    @Test
    void updateItemQuantity_ReturnsOrder() throws Exception {
        when(orderService.updateItemQuantity(1L, 1L, 5)).thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(patch("/orders/{orderId}/items/{itemId}/quantity", 1L, 1L)
                        .param("quantity", "5")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exampleOrderDto.id()));
    }

    @Test
    void checkout_ReturnsOrder() throws Exception {
        when(orderService.checkout(1L)).thenReturn(exampleOrderDto.toEntity());

        mockMvc.perform(post("/orders/{id}/checkout", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exampleOrderDto.id()));
    }
}

