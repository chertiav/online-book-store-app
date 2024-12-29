package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.cart.item.UpdateCartItemRequestDto;

public interface CartItemService {
    void updateCartItem(Long cartItemId, UpdateCartItemRequestDto requestDto);

    void deleteCartItem(Long cartItemId);
}
