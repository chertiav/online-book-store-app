package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.dto.cart.item.UpdateCartItemRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.CartItemMapper;
import com.achdev.onlinebookstoreapp.model.CartItem;
import com.achdev.onlinebookstoreapp.repository.cart.item.CartItemRepository;
import com.achdev.onlinebookstoreapp.service.CartItemService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public Optional<CartItem> findCartItemByBookIdAndShoppingCartId(
            Long bookId, Long shoppingCartId) {
        return cartItemRepository.findCartItemByBookIdAndShoppingCartId(bookId, shoppingCartId);
    }

    @Override
    public void updateCartItem(Long cartItemId, UpdateCartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Can't find cart item by id: " + cartItemId));
        cartItemMapper.updateCartItemQuantity(requestDto, cartItem);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
