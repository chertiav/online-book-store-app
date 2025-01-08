package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.dto.order.OrderDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderRequestDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderStatusRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.OrderMapper;
import com.achdev.onlinebookstoreapp.model.Order;
import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import com.achdev.onlinebookstoreapp.repository.order.OrderRepository;
import com.achdev.onlinebookstoreapp.service.OrderService;
import com.achdev.onlinebookstoreapp.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderDto completeOrder(String userEmail, OrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartByUserEmail(userEmail);
        Order order = orderRepository.save(orderMapper.toModel(requestDto, shoppingCart));
        shoppingCartService.clearShoppingCart(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> findAllOrdersByUserEmail(String email, Pageable pageable) {
        return orderRepository.findByUserEmail(email, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public OrderDto updateStatusById(Long id, OrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found by id: " + id));
        orderMapper.updateStatusOrderFromDto(requestDto, order);
        return orderMapper.toDto(orderRepository.save(order));
    }
}
