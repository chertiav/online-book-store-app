package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.order.OrderDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderRequestDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderStatusRequestDto;
import com.achdev.onlinebookstoreapp.dto.order.item.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto completeOrder(String userEmail, OrderRequestDto requestDto);

    Page<OrderDto> findAllOrdersByUserEmail(String email, Pageable pageable);

    OrderDto updateStatusById(Long id, OrderStatusRequestDto requestDto);

    Page<OrderItemDto> findAllOrderItemsByOrderIdAndOrderUserEmail(
            Long orderId, String email, Pageable pageable);

    OrderItemDto findOrderItemByIdAndOrderIdAndOrderUserEmail(
            Long itemId, Long orderId, String userEmail);
}
