package com.achdev.onlinebookstoreapp.repository.order.item;

import com.achdev.onlinebookstoreapp.model.OrderItem;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findAllOrderItemsByOrderIdAndOrderUserEmail(
            Long orderId, String userEmail, Pageable pageable);

    Optional<OrderItem> findOrderItemByIdAndOrderIdAndOrderUserEmail(
            Long itemId, Long orderId, String userEmail);
}
