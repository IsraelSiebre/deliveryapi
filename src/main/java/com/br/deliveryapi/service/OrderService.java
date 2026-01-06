package com.br.deliveryapi.service;

import com.br.deliveryapi.dto.order.OrderItemDto;
import com.br.deliveryapi.entity.Order;
import com.br.deliveryapi.entity.OrderItem;
import com.br.deliveryapi.enums.OrderStatus;
import com.br.deliveryapi.repository.OrderItemRepository;
import com.br.deliveryapi.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order create(Order order) {
        order.setOrderStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);
        calculateTotalPrice(order.getId());
        return order;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order Not Found"));
    }

    public List<Order> findAllByClientId(Long clientId) {
        return orderRepository.findAllByClientId(clientId);
    }

    public List<Order> findAllByOrderStatus(OrderStatus status) {
        return orderRepository.findAllByOrderStatus(status);
    }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    public Order update(Long id, Order newOrder) {
        Order order = findById(id);

        order.setOrderStatus(newOrder.getOrderStatus());
        order.setPayMethod(newOrder.getPayMethod());
        order.setDeliveryOption(newOrder.getDeliveryOption());

        Order updated = orderRepository.save(order);
        calculateTotalPrice(updated.getId());

        return updated;
    }

    public void deleteById(Long id) {
        Order order = findById(id);
        orderRepository.delete(order);
    }

    public void calculateTotalPrice(Long orderId) {
        Order order = findById(orderId);
        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);

        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setPrice(total);
        orderRepository.save(order);
    }

    public Order updateItemQuantity(Long orderId, Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity Must Be Greater Than 0");
        }

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item Not Found"));

        item.setQuantity(quantity);
        item.calculateTotalPrice();
        orderItemRepository.save(item);

        calculateTotalPrice(orderId);
        return findById(orderId);
    }

    public Order addItem(Long orderId, OrderItemDto itemDto) {
        if (itemDto.quantity() == null || itemDto.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity Must Be Greater Than 0");
        }

        Order order = findById(orderId);

        boolean exists = orderItemRepository.existsByOrderIdAndProductId(orderId, itemDto.product().getId());
        if (exists) {
            throw new IllegalStateException("Item Alredy Exists");
        }

        OrderItem item = itemDto.toEntity();
        item.setOrder(order);
        item.calculateTotalPrice();

        orderItemRepository.save(item);
        calculateTotalPrice(orderId);
        return findById(orderId);
    }

    public Order removeItem(Long orderId, Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item Not Found"));

        orderItemRepository.delete(item);
        calculateTotalPrice(orderId);
        return findById(orderId);
    }

    public List<Order> findByPeriod(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end);
    }

    public Order checkout(Long orderId) {
        Order order = findById(orderId);

        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        if (items.isEmpty()) {
            throw new IllegalStateException("Order Can Not Be Null");
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }
}
