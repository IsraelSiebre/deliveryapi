package com.br.deliveryapi.repository;

import com.br.deliveryapi.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
    List<OrderItem> findAllByOrderId(Long id);

}
