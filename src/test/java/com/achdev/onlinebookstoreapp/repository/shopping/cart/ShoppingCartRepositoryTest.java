package com.achdev.onlinebookstoreapp.repository.shopping.cart;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_THREE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_THREE_EMAIL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_TWO;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_TWO_EMAIL;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createCartToModelForUser;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createTestShoppingCart;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createTestUser;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getCartItemsByUserId;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getTestUserFromDb;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllCartItems;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapBookToCartItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.achdev.onlinebookstoreapp.model.CartItem;
import com.achdev.onlinebookstoreapp.model.ShoppingCart;
import com.achdev.onlinebookstoreapp.model.User;
import com.achdev.onlinebookstoreapp.repository.user.UserRepository;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("ShoppingCartRepository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    private static List<CartItem> cartItems;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        setupDatabase(dataSource);
        cartItems = loadAllCartItems();
    }

    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/user/add-user-to-users-table.sql",
                    "database/role/add-user_role-to-roles-table.sql",
                    "database/cart/add-cart-to-shopping_carts-table.sql"
            );
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/cart/remove-all-from-shopping_carts-table.sql",
                    "database/role/remove-user_role-from-roles-table.sql",
                    "database/user/remove-user-from-users-table.sql"
            );
        }
    }

    @DisplayName("Should save shopping cart with valid data and return expected result")
    @Sql(scripts = "classpath:database/cart/remove-cart_user_id_two-from-shopping_carts-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void save_ValidData_ShouldReturnShoppingCart() {
        //Given
        User user = getTestUserFromDb(userRepository, TEST_USER_ID_TWO);
        ShoppingCart cartToModel = createCartToModelForUser(user);
        ShoppingCart expected = createTestShoppingCart(user);

        //When
        ShoppingCart actual = shoppingCartRepository.save(cartToModel);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @DisplayName("Find by user email should return ShoppingCart when a valid email is provided")
    @Sql(scripts = {
            "classpath:database/books/add-books-to-books-table.sql",
            "classpath:database/category/add-categories-to-categories-table.sql",
            "classpath:database/books/categories/add-all-to_books_categories-table.sql",
            "classpath:database/cart/item/add-items-to-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cart/item/remove-all-from-cart_items-table.sql",
            "classpath:database/books/categories/remove-all-from-book_categories-table.sql",
            "classpath:database/books/remove-all-books-from-books-table.sql",
            "classpath:database/category/remove-categories-from-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @MethodSource("provideCartData")
    @ParameterizedTest
    void findByUserEmail_ValidUserEmail_ShouldReturnShoppingCart(
            User user,
            Set<CartItem> expectedCartItems
    ) {
        //Given
        ShoppingCart expectedCart = createCartToModelForUser(user);
        expectedCart.setId(user.getId());

        //When
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserEmail(user.getEmail());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expectedCart, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(expectedCartItems.size(), actual.get().getCartItems().size(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                mapBookToCartItem(expectedCartItems),
                mapBookToCartItem(actual.get().getCartItems()),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    private static Stream<Arguments> provideCartData() {
        User userIdTwo = createTestUser(TEST_USER_ID_TWO, TEST_USER_ID_TWO_EMAIL);
        User userIdThree = createTestUser(TEST_USER_ID_THREE, TEST_USER_ID_THREE_EMAIL);
        return Stream.of(
                Arguments.of(userIdTwo, getCartItemsByUserId(userIdTwo.getId(), cartItems)),
                Arguments.of(userIdThree, getCartItemsByUserId(userIdThree.getId(), cartItems))
        );
    }
}
