package com.br.deliveryapi.repository;

import com.br.deliveryapi.entity.Order;
import com.br.deliveryapi.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByClientId(Long id);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
}
