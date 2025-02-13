package com.achdev.onlinebookstoreapp.repository.cart.item;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.BOOK_QUANTITY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CART_ITEMS_TABLE_NAME;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_CART_ITEM_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_BOOK_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.USER_ID_TWO_SHOPPING_CART_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.VALID_CART_ITEM_ID;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createCartItem;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getDeleteCheckMessage;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getNotFoundMessage;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllCartItems;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.recordExistsInDatabase;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.setDataBookByBookId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.achdev.onlinebookstoreapp.model.CartItem;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("CartItemRepositoryTest Integration Test")
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemRepositoryTest {
    private static final String CART_ITEM = "CartItem";
    private static List<CartItem> cartItems;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                    "database/books/add-books-to-books-table.sql",
                    "database/category/add-categories-to-categories-table.sql",
                    "database/books/categories/add-all-to_books_categories-table.sql",
                    "database/cart/add-cart-to-shopping_carts-table.sql",
                    "database/cart/item/add-items-to-cart_items-table.sql"
            );
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/cart/item/remove-all-from-cart_items-table.sql",
                    "database/books/categories/remove-all-from-book_categories-table.sql",
                    "database/category/remove-categories-from-categories-table.sql",
                    "database/books/remove-all-books-from-books-table.sql",
                    "database/cart/remove-all-from-shopping_carts-table.sql",
                    "database/role/remove-user_role-from-roles-table.sql",
                    "database/user/remove-user-from-users-table.sql"
            );
        }
    }

    @Order(1)
    @DisplayName("Find cart item by bookId and shoppingCartId when a valid data is provided")
    @MethodSource("provideBookIdAndCartIdData")
    @ParameterizedTest
    void findCartItemByBookIdAndCartId_ValidData_ShouldReturnCartItem(
            Long bookId,
            Long cartId,
            CartItem expected
    ) {
        //When
        Optional<CartItem> actual = cartItemRepository
                .findCartItemByBookIdAndShoppingCartId(bookId, cartId);

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.getShoppingCart().getId(),
                actual.get().getShoppingCart().getId(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Order(2)
    @Sql(scripts = "classpath:database/cart/item/remove-cartItem_id_nine-from-cart_items-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Save CartItem successfully when valid data is provided")
    @Test
    void save_ValidData_ShouldReturnCartItem() {
        //Given
        CartItem cartItemToModel = createCartItem(null,
                USER_ID_TWO_SHOPPING_CART_ID,
                SAMPLE_BOOK_ID,
                BOOK_QUANTITY);
        CartItem expected = createCartItem(
                NEW_CART_ITEM_ID,
                USER_ID_TWO_SHOPPING_CART_ID,
                SAMPLE_BOOK_ID,
                BOOK_QUANTITY);

        //When
        CartItem actual = cartItemRepository.save(cartItemToModel);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, ID_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertNotNull(actual.getId());
    }

    @DisplayName("Find cart item by Id when a valid data is provided")
    @MethodSource("provideCartItemIdData")
    @ParameterizedTest
    void findById_ValidId_ShouldReturnCartItem(Long id, CartItem expected) {
        //When
        Optional<CartItem> actual = cartItemRepository.findById(id);

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.getShoppingCart().getId(),
                actual.get().getShoppingCart().getId(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Order(3)
    @Sql(scripts = "classpath:database/cart/item/add-item-for-delete-to-cart_items-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete cart item successfully when valid ID is provided")
    @Test
    void deleteById_ValidId_ShouldDeleteCartItem() {
        //Given
        boolean existsBefore = recordExistsInDatabase(jdbcTemplate,
                CART_ITEMS_TABLE_NAME, VALID_CART_ITEM_ID);

        // When
        cartItemRepository.deleteById(VALID_CART_ITEM_ID);

        // Then
        Optional<CartItem> cartItemResultAfter = cartItemRepository.findById(VALID_CART_ITEM_ID);

        assertTrue(existsBefore, getDeleteCheckMessage(CART_ITEM, VALID_CART_ITEM_ID));
        assertTrue(cartItemResultAfter.isEmpty(),
                getNotFoundMessage(CART_ITEM, VALID_CART_ITEM_ID));
    }

    @Order(4)
    @Test
    @DisplayName("Should not delete cart item when invalid ID is provided")
    void deleteById_InvalidId_ShouldNotDeleteAnyCartItem() {
        // Given
        boolean existsBefore = recordExistsInDatabase(jdbcTemplate,
                CART_ITEMS_TABLE_NAME, INVALID_ID);

        // When
        cartItemRepository.deleteById(INVALID_ID);

        // Then
        Optional<CartItem> cartItemResultAfter = cartItemRepository.findById(INVALID_ID);

        assertFalse(existsBefore, getNotFoundMessage(CART_ITEM, INVALID_ID));
        assertTrue(cartItemResultAfter.isEmpty(), getNotFoundMessage(CART_ITEM, INVALID_ID));
    }

    private static Stream<Arguments> provideBookIdAndCartIdData() {
        List<Arguments> arguments = new ArrayList<>();
        cartItems.forEach(cartItem -> arguments.add(
                Arguments.of(
                        cartItem.getBook().getId(),
                        cartItem.getShoppingCart().getId(),
                        setDataBookByBookId(cartItem))
        ));
        return arguments.stream();
    }

    private static Stream<Arguments> provideCartItemIdData() {
        List<Arguments> arguments = new ArrayList<>();
        cartItems.forEach(cartItem -> arguments.add(
                Arguments.of(cartItem.getId(), setDataBookByBookId(cartItem))
        ));
        return arguments.stream();
    }
}
