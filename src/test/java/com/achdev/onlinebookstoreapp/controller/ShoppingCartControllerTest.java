package com.achdev.onlinebookstoreapp.controller;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACCESS_DENIED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.BOOK_QUANTITY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CARTS_ENDPOINT;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EIGHTH_BOOK_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_FIELD_BOOK_ID_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_FIELD_QUANTITY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_BOOK_ID_POSITIVE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_BOOK_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_CART_ITEM_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_QUANTITY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_BOOK_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_CART_ITEM_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PATH_SEPARATOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_TEST_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_TWO;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_TWO_EMAIL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TIMESTAMP_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.UPDATED_BOOK_QUANTITY;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createCartItemRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createCartItemResponseDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createErrorDetailMap;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createErrorResponse;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createShoppingCartDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createUpdateCartItemRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getCartItemsByUserId;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllCartItems;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapBookToCartItem;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapMvcResultToObjectDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.parseErrorResponseFromMvcResult;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.setDataBookByBookId;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemResponseDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.UpdateCartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.shopping.cart.ShoppingCartDto;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.model.CartItem;
import com.achdev.onlinebookstoreapp.repository.cart.item.CartItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("ShoppingCartControllerTest Integration Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static Set<CartItem> cartItems;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeAll
    static void setUp(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        setupDatabase(dataSource);
        cartItems = mapBookToCartItem(new HashSet<>(loadAllCartItems()));
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

    @WithUserDetails(TEST_USER_ID_TWO_EMAIL)
    @Order(1)
    @DisplayName("Get shopping cart")
    @Test
    void getShoppingCart_ValidData_ShouldReturnShoppingCartDto() throws Exception {
        //Given
        List<CartItemResponseDto> cartItemResponseDto = createCartItemResponseDto(
                getCartItemsByUserId(TEST_USER_ID_TWO, cartItems.stream().toList())
        );
        ShoppingCartDto expected = createShoppingCartDto(TEST_USER_ID_TWO, cartItemResponseDto);

        //When
        MvcResult result = mockMvc.perform(get(CARTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        ShoppingCartDto actual = mapMvcResultToObjectDto(result, objectMapper,
                ShoppingCartDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Get shopping cart should return forbidden")
    @Order(2)
    @Test
    void getShoppingCart_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc.perform(get(CARTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithUserDetails(TEST_USER_ID_TWO_EMAIL)
    @Order(3)
    @DisplayName("Add item to shopping cart when valid data is provided")
    @Test
    void addCartItem_ValidData_ShouldReturnShoppingCartDto() throws Exception {
        //Given
        Book book = loadAllBooks().get(EIGHTH_BOOK_INDEX);
        CartItemResponseDto newCartItemResponse = new CartItemResponseDto(
                NEW_CART_ITEM_ID, book.getId(),
                book.getTitle(), BOOK_QUANTITY);

        List<CartItemResponseDto> cartItemResponseDtos = new ArrayList<>(createCartItemResponseDto(
                getCartItemsByUserId(TEST_USER_ID_TWO, cartItems.stream().toList())
        ));
        cartItemResponseDtos.add(newCartItemResponse);
        cartItemResponseDtos.sort(Comparator.comparing(CartItemResponseDto::id));

        CartItemRequestDto requestDto = createCartItemRequestDto(book.getId(), BOOK_QUANTITY);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        ShoppingCartDto expected = createShoppingCartDto(TEST_USER_ID_TWO, cartItemResponseDtos);

        //When
        MvcResult result = mockMvc.perform(post(CARTS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        ShoppingCartDto actual = mapMvcResultToObjectDto(result, objectMapper,
                ShoppingCartDto.class);
        actual.getCartItems().sort(Comparator.comparing(CartItemResponseDto::id));

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(TEST_USER_ID_TWO_EMAIL)
    @Order(4)
    @DisplayName("Add item to cart when non positive bookId is provided should return bad request")
    @Test
    void addCartItem_NonPositiveBookId_ShouldReturnBadRequest() throws Exception {
        //Given
        CartItemRequestDto requestDto = createCartItemRequestDto(INVALID_BOOK_ID, BOOK_QUANTITY);

        Map<String, String> errorDetailDto = createErrorDetailMap(
                ERROR_FIELD_BOOK_ID_VALUE,
                ERROR_MESSAGE_BOOK_ID_POSITIVE
        );
        CommonApiErrorResponse expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(CARTS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithUserDetails(TEST_USER_ID_TWO_EMAIL)
    @Order(5)
    @DisplayName("Add item to shopping when invalid bookId is provided should return not found")
    @Test
    void addCartItem_InvalidBookId_ShouldReturnNotFound() throws Exception {
        //Given
        CartItemRequestDto requestDto = createCartItemRequestDto(INVALID_ID, BOOK_QUANTITY);

        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_BOOK_NOT_FOUND + INVALID_ID);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(CARTS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Add item to shopping cart should return forbidden")
    @Order(6)
    @Test
    void addCartItem_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CartItemRequestDto requestDto = createCartItemRequestDto(SAMPLE_TEST_ID, BOOK_QUANTITY);

        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(CARTS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithUserDetails(TEST_USER_ID_TWO_EMAIL)
    @Order(7)
    @DisplayName("Update cartItem when valid data is provided")
    @Test
    void updateCartItem_ValidData_ShouldCartItemResponseDto() throws Exception {
        CartItem cartItem = setDataBookByBookId(cartItems.stream().toList().get(INITIAL_INDEX));

        UpdateCartItemRequestDto requestDto = createUpdateCartItemRequestDto(UPDATED_BOOK_QUANTITY);

        CartItemResponseDto expected = new CartItemResponseDto(
                cartItem.getId(), cartItem.getBook().getId(),
                cartItem.getBook().getTitle(), UPDATED_BOOK_QUANTITY);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CARTS_ENDPOINT + PATH_SEPARATOR + cartItem.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CartItemResponseDto actual = mapMvcResultToObjectDto(result, objectMapper,
                CartItemResponseDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(TEST_USER_ID_TWO_EMAIL)
    @Order(8)
    @DisplayName("Update cartItem when invalid data is provided should return bad request")
    @Test
    void updateCartItem_InvalidData_ShouldReturnBadRequest() throws Exception {
        CartItem cartItem = cartItems.stream().toList().get(INITIAL_INDEX);

        UpdateCartItemRequestDto requestDto = createUpdateCartItemRequestDto((int) INVALID_BOOK_ID);

        Map<String, String> errorDetailDto = createErrorDetailMap(
                ERROR_FIELD_QUANTITY,
                ERROR_MESSAGE_QUANTITY
        );
        CommonApiErrorResponse expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CARTS_ENDPOINT + PATH_SEPARATOR + cartItem.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithUserDetails(TEST_USER_ID_TWO_EMAIL)
    @Order(9)
    @DisplayName("Update cartItem when invalid Id is provided should return not found")
    @Test
    void updateCartItem_InvalidId_ShouldReturnNotFound() throws Exception {
        UpdateCartItemRequestDto requestDto = createUpdateCartItemRequestDto(UPDATED_BOOK_QUANTITY);

        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_CART_ITEM_NOT_FOUND + INVALID_ID);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CARTS_ENDPOINT + PATH_SEPARATOR + INVALID_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update cartItem should return forbidden")
    @Order(10)
    @Test
    void updateCartItem_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        UpdateCartItemRequestDto requestDto = createUpdateCartItemRequestDto(UPDATED_BOOK_QUANTITY);

        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CARTS_ENDPOINT + PATH_SEPARATOR + INVALID_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithMockUser(TEST_USER_ID_TWO_EMAIL)
    @DisplayName("Delete cartItem when valid data is provided")
    @Order(11)
    @Test
    void deleteCartItem_ValidData_ShouldReturnNoContent() throws Exception {
        CartItem cartItem = cartItems.stream().toList().get(INITIAL_INDEX);

        //When
        mockMvc.perform(delete(CARTS_ENDPOINT + PATH_SEPARATOR + cartItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        assertFalse(cartItemRepository.existsById(cartItem.getId()),
                OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete cartItem when should return forbidden")
    @Order(12)
    @Test
    void deleteCartItem_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CartItem cartItem = cartItems.stream().toList().get(INITIAL_INDEX);
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(delete(CARTS_ENDPOINT + PATH_SEPARATOR + cartItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }
}
