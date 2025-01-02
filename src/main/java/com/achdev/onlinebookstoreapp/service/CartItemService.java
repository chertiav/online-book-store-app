package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.cart.item.UpdateCartItemRequestDto;
import com.achdev.onlinebookstoreapp.model.CartItem;
import java.util.Optional;

public interface CartItemService {
    Optional<CartItem> findCartItemByBookIdAndShoppingCartId(Long bookId, Long shoppingCartId);

    void updateCartItem(Long cartItemId, UpdateCartItemRequestDto requestDto);

    void deleteCartItem(Long cartItemId);
}
