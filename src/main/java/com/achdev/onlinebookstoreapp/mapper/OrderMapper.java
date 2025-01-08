package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.order.OrderDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderRequestDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderStatusRequestDto;
import com.achdev.onlinebookstoreapp.model.Order;
import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shippingAddress", source = "requestDto.shippingAddress")
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(Order.Status.PENDING)")
    @Mapping(target = "total", expression = "java(calculateTotal(shoppingCart))")
    @Mapping(target = "orderItems",
            source = "shoppingCart.cartItems",
            qualifiedByName = "mapCartItemsToOrderItems")
    Order toModel(OrderRequestDto requestDto, ShoppingCart shoppingCart);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItems",
            source = "orderItems",
            qualifiedByName = "mapOrderItemToOrderItemDto")
    OrderDto toDto(Order order);

    void updateStatusOrderFromDto(OrderStatusRequestDto requestDto, @MappingTarget Order order);

    @Named("calculateTotal")
    default BigDecimal calculateTotal(ShoppingCart shoppingCart) {
        if (shoppingCart == null || shoppingCart.getCartItems() == null) {
            return BigDecimal.ZERO;
        }
        return shoppingCart.getCartItems().stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
