package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.UpdateCartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.shopping.cart.ShoppingCartDto;
import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import com.achdev.onlinebookstoreapp.model.User;

public interface ShoppingCartService {
    ShoppingCartDto findShoppingCartByUserEmail(String email);

    void registerShoppingCart(User user);

    ShoppingCartDto addCartItem(CartItemRequestDto requestDto, String userEmail);

    ShoppingCart getShoppingCartByUserEmail(String email);

    void clearShoppingCart(ShoppingCart shoppingCart);

    void updateCartItem(Long cartItemId, UpdateCartItemRequestDto requestDto);

    void deleteCartItem(Long cartItemId);
}
