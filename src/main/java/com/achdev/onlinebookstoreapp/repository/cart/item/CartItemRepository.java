package com.achdev.onlinebookstoreapp.repository.cart.item;

import com.achdev.onlinebookstoreapp.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findCartItemByBookIdAndShoppingCartId(Long bookId, Long shoppingCartId);
}
