package com.achdev.onlinebookstoreapp.mapper;

import com.achdev.onlinebookstoreapp.config.MapperConfig;
import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemResponseDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.UpdateCartItemRequestDto;
import com.achdev.onlinebookstoreapp.model.CartItem;
import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(target = "book", source = "bookDto", qualifiedByName = "bookByBookDto")
    @Mapping(target = "shoppingCart", source = "shoppingCart")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "id", ignore = true)
    CartItem toModel(BookDto bookDto, int quantity, ShoppingCart shoppingCart);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemResponseDto toDto(CartItem cartItem);

    void updateCartItemQuantity(
            UpdateCartItemRequestDto requestDto,
            @MappingTarget CartItem cartItem
    );

    @Named("cartItemResponseDtoToList")
    default List<CartItemResponseDto> cartItemResponseToList(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toDto)
                .toList();
    }
}
