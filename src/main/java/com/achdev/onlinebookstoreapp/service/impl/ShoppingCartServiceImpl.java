package com.achdev.onlinebookstoreapp.service.impl;

import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.UpdateCartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.shopping.cart.ShoppingCartDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.CartItemMapper;
import com.achdev.onlinebookstoreapp.mapper.ShoppingCartMapper;
import com.achdev.onlinebookstoreapp.model.CartItem;
import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import com.achdev.onlinebookstoreapp.model.User;
import com.achdev.onlinebookstoreapp.repository.cart.item.CartItemRepository;
import com.achdev.onlinebookstoreapp.repository.shopping.cart.ShoppingCartRepository;
import com.achdev.onlinebookstoreapp.service.BookService;
import com.achdev.onlinebookstoreapp.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository shoppingCartsRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookService bookService;

    @Transactional
    @Override
    public void registerShoppingCart(User user) {
        shoppingCartsRepository.save(shoppingCartMapper.toModel(user));
    }

    @Override
    public ShoppingCartDto findShoppingCartByUserEmail(String email) {
        return shoppingCartMapper.toDto(getShoppingCartByUserEmail(email));
    }

    @Transactional
    @Override
    public ShoppingCartDto addCartItem(CartItemRequestDto requestDto, String userEmail) {
        BookDto bookDto = bookService.findById(requestDto.getBookId());
        ShoppingCart shoppingCart = getShoppingCartByUserEmail(userEmail);
        if (cartItemRepository.findCartItemByBookIdAndShoppingCartId(
                bookDto.getId(), shoppingCart.getId()).isEmpty()
        ) {
            CartItem cartItem = cartItemMapper.toModel(
                    bookDto, requestDto.getQuantity(), shoppingCart);
            shoppingCart.getCartItems().add(cartItemRepository.save(cartItem));
        }
        return shoppingCartMapper.toDto(shoppingCartsRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCart getShoppingCartByUserEmail(String email) {
        return shoppingCartsRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user's shopping cart by email: " + email));
    }

    @Override
    public void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems().clear();
        shoppingCartsRepository.save(shoppingCart);
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
