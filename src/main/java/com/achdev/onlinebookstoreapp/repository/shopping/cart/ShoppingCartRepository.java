package com.achdev.onlinebookstoreapp.repository.shopping.cart;

import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph(attributePaths = {"cartItems", "cartItems.book"})
    Optional<ShoppingCart> findByUserEmail(String userEmail);

    //    @EntityGraph(attributePaths = {"cartItems", "cartItems.book"})
    //    ShoppingCart save(ShoppingCart shoppingCart);
}
