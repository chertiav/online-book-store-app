package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.dto.order.item.OrderItemDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.OrderItemMapper;
import com.achdev.onlinebookstoreapp.repository.order.item.OrderItemRepository;
import com.achdev.onlinebookstoreapp.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Page<OrderItemDto> findAllOrderItemsByOrderIdAndOrderUserEmail(
            Long orderId, String email, Pageable pageable) {
        return orderItemRepository.findAllOrderItemsByOrderIdAndOrderUserEmail(
                        orderId, email, pageable)
                .map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto findOrderItemByIdAndOrderIdAndOrderUserEmail(
            Long itemId, Long orderId, String userEmail) {
        return orderItemRepository
                .findOrderItemByIdAndOrderIdAndOrderUserEmail(itemId, orderId, userEmail)
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order item by id: "
                        + itemId));
    }
}
