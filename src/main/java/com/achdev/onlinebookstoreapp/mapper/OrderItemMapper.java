package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.order.item.OrderItemDto;
import com.achdev.onlinebookstoreapp.model.CartItem;
import com.achdev.onlinebookstoreapp.model.Order;
import com.achdev.onlinebookstoreapp.model.OrderItem;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", source = "book.price")
    @Mapping(target = "order", ignore = true)
    OrderItem toModel(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    @Named("mapCartItemsToOrderItems")
    default Set<OrderItem> getOrderItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Named("mapOrderItemToOrderItemDto")
    default List<OrderItemDto> getOrderItemDto(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDto)
                .toList();
    }

    @AfterMapping
    default void setOrder(@MappingTarget Order order) {
        if (order != null && order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }
    }
}
