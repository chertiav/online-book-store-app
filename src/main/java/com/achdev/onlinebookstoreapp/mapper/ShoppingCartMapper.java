package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.shopping.cart.ShoppingCartDto;
import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import com.achdev.onlinebookstoreapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    ShoppingCart toModel(User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", source = "cartItems",
            qualifiedByName = "cartItemResponseDtoToList")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
