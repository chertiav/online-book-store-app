package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.order.item.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {
    Page<OrderItemDto> findAllOrderItemsByOrderIdAndOrderUserEmail(
            Long orderId, String email, Pageable pageable);

    OrderItemDto findOrderItemByIdAndOrderIdAndOrderUserEmail(
            Long itemId, Long orderId, String userEmail);
}
