package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.order.OrderDto;
import com.br.deliveryapi.dto.order.OrderItemDto;
import com.br.deliveryapi.dto.order.PeriodRequestDto;
import com.br.deliveryapi.entity.Order;
import com.br.deliveryapi.enums.OrderStatus;
import com.br.deliveryapi.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // --- ORDER CRUD ---

    @PostMapping("/")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto dto) {
        OrderDto created = orderService.create(dto.toEntity()).toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.findAll().stream().map(o -> o.toDto()).toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id).toDto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto.toEntity()).toDto());
    }

    @PatchMapping("{id}/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status).toDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- ORDER FILTERS ---

    @GetMapping("/client/{id}")
    public ResponseEntity<List<OrderDto>> getOrdersByClientId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findAllByClientId(id).stream().map(o -> o.toDto()).toList());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.findAllByOrderStatus(status).stream().map(o -> o.toDto()).toList());
    }

    @PostMapping("/period")
    public ResponseEntity<List<OrderDto>> getOrdersByPeriod(@RequestBody PeriodRequestDto period) {
        List<OrderDto> orders = orderService.findByPeriod(period.start(), period.end())
                .stream()
                .map(Order::toDto)
                .toList();
        return ResponseEntity.ok(orders);
    }


    // --- ORDER ITEMS ---

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto> addItemToOrder(@PathVariable Long orderId, @RequestBody OrderItemDto itemDto) {
        return ResponseEntity.ok(orderService.addItem(orderId, itemDto).toDto());
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> removeItemFromOrder(@PathVariable Long orderId, @PathVariable Long itemId) {
        return ResponseEntity.ok(orderService.removeItem(orderId, itemId).toDto());
    }

    @PatchMapping("/{orderId}/items/{itemId}/quantity")
    public ResponseEntity<OrderDto> updateItemQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam("quantity") int quantity) {
        return ResponseEntity.ok(orderService.updateItemQuantity(orderId, itemId, quantity).toDto());
    }

    // --- CHECKOUT ---

    @PostMapping("/{id}/checkout")
    public ResponseEntity<OrderDto> checkout(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.checkout(id).toDto());
    }
}
