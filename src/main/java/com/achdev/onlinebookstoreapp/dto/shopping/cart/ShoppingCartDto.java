package com.achdev.onlinebookstoreapp.dto.shopping.cart;

import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemResponseDto;
import java.util.List;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private List<CartItemResponseDto> cartItems;
}
