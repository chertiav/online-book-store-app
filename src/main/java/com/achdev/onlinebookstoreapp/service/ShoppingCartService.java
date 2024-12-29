package com.achdev.onlinebookstoreapp.service;

import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.shopping.cart.ShoppingCartDto;
import com.achdev.onlinebookstoreapp.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface ShoppingCartService {
    ShoppingCartDto findShoppingCartByUserEmail(String email);

    void registerShoppingCart(User user);

    void addCartItem(CartItemRequestDto requestDto, UserDetails userDetails);
}
