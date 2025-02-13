package com.achdev.onlinebookstoreapp.service.impl;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.BOOK_QUANTITY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_BOOK_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_CART_ITEM_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_CART_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_EMAIL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_TEST_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SHOULD_BE_EMPTY_AFTER_CLEARING_THE_SHOPPING_CART;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_TWO;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TEST_USER_ID_TWO_EMAIL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.UPDATED_BOOK_QUANTITY;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createTestCartItem;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createTestCartItemRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createTestShoppingCart;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createTestUser;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.initializeTestShoppingCart;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapBookToDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapShoppingCartToDto;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemRequestDto;
import com.achdev.onlinebookstoreapp.dto.cart.item.CartItemResponseDto;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Shopping Cart Service Implementation Test")
class ShoppingCartServiceImplTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private ShoppingCartRepository shoppingCartsRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookService bookService;

    @Test
    @DisplayName("Save shopping cart successfully when valid data is provided")
    void registerShoppingCart_ValidData_SuccessfullySavedShoppingCart() {
        //Given
        User user = createTestUser(TEST_USER_ID_TWO, TEST_USER_ID_TWO_EMAIL);
        ShoppingCart cart = createTestShoppingCart(user);

        when(shoppingCartMapper.toModel(user)).thenReturn(cart);

        //When
        shoppingCartService.registerShoppingCart(user);

        //Then
        verify(shoppingCartMapper).toModel(user);
        verify(shoppingCartsRepository).save(cart);
        verifyNoMoreInteractions(shoppingCartMapper, shoppingCartsRepository);
    }

    @Test
    @DisplayName("Find by user email should return ShoppingCartDto when a valid email is provided")
    void findShoppingCartByUserEmail_ValidUserEmail_ShouldReturnShoppingCartDto() {
        //Given
        User user = createTestUser(TEST_USER_ID_TWO, TEST_USER_ID_TWO_EMAIL);
        ShoppingCart cart = createTestShoppingCart(user);
        ShoppingCartDto expected = mapShoppingCartToDto(cart);

        when(shoppingCartsRepository.findByUserEmail(user.getEmail()))
                .thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(cart)).thenReturn(expected);

        //When
        ShoppingCartDto actual = shoppingCartService.findShoppingCartByUserEmail(user.getEmail());

        //Then
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(shoppingCartsRepository).findByUserEmail(user.getEmail());
        verify(shoppingCartMapper).toDto(cart);
        verifyNoMoreInteractions(shoppingCartMapper, shoppingCartsRepository);
    }

    @Test
    @DisplayName("Find by user email should throw exception when the email is invalid")
    void findShoppingCartByUserEmail_InvalidUserEmail_ShouldReturnException() {
        //Given
        when(shoppingCartsRepository.findByUserEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.findShoppingCartByUserEmail(INVALID_EMAIL));

        //Then
        String expected = ERROR_MESSAGE_CART_NOT_FOUND + INVALID_EMAIL;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(shoppingCartsRepository).findByUserEmail(INVALID_EMAIL);
        verifyNoMoreInteractions(shoppingCartMapper, shoppingCartsRepository);
    }

    @Test
    @DisplayName("Add an item into shopping cart when a valid data is provided")
    void addCartItem_ValidData_ShouldReturnShoppingCartDto() {
        //Given
        User user = createTestUser(TEST_USER_ID_TWO, TEST_USER_ID_TWO_EMAIL);
        CartItemRequestDto requestDto = createTestCartItemRequestDto();
        ShoppingCart cart = createTestShoppingCart(user);
        BookDto bookDto = mapBookToDto(loadAllBooks().get(INITIAL_INDEX));

        CartItem cartItem = createTestCartItem(bookDto, requestDto.getQuantity(), cart);
        cartItem.setId(SAMPLE_TEST_ID);

        ShoppingCart expectedCart = createTestShoppingCart(user);
        expectedCart.setCartItems(Set.of(cartItem));

        CartItem cartItemToModel = createTestCartItem(bookDto, requestDto.getQuantity(), cart);

        when(bookService.findById(requestDto.getBookId())).thenReturn(bookDto);
        when(shoppingCartsRepository.findByUserEmail(user.getEmail()))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemByBookIdAndShoppingCartId(
                bookDto.getId(),
                cart.getId()
        )).thenReturn(Optional.empty());
        when(cartItemMapper.toModel(bookDto, requestDto.getQuantity(), cart))
                .thenReturn(cartItemToModel);
        when(cartItemRepository.save(cartItemToModel)).thenReturn(cartItem);
        when(shoppingCartsRepository.save(cart)).thenReturn(cart);
        when(shoppingCartMapper.toDto(cart)).thenReturn(mapShoppingCartToDto(expectedCart));

        //When
        ShoppingCartDto actual = shoppingCartService.addCartItem(requestDto, user.getEmail());

        //Then
        ShoppingCartDto expected = mapShoppingCartToDto(cart);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(bookService).findById(requestDto.getBookId());
        verify(shoppingCartsRepository).findByUserEmail(user.getEmail());
        verify(cartItemRepository).findCartItemByBookIdAndShoppingCartId(
                bookDto.getId(),
                cart.getId()
        );
        verify(cartItemMapper).toModel(bookDto, requestDto.getQuantity(), cart);
        verify(cartItemRepository).save(cartItemToModel);
        verify(shoppingCartsRepository).save(cart);
        verifyNoMoreInteractions(
                bookService,
                shoppingCartsRepository,
                cartItemRepository,
                cartItemMapper
        );
    }

    @Test
    @DisplayName("Add an item into shopping cart should throw exception when the bookId is invalid")
    void addCartItem_InvalidBookId_ShouldReturnException() {
        //Given
        User user = createTestUser(TEST_USER_ID_TWO, TEST_USER_ID_TWO_EMAIL);
        CartItemRequestDto requestDto = createTestCartItemRequestDto();
        requestDto.setBookId(INVALID_ID);

        when(bookService.findById(INVALID_ID)).thenThrow(
                new EntityNotFoundException(ERROR_MESSAGE_BOOK_NOT_FOUND + INVALID_ID));

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addCartItem(requestDto, user.getEmail()));

        //INVALID_ID
        String expected = ERROR_MESSAGE_BOOK_NOT_FOUND + INVALID_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(bookService).findById(INVALID_ID);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    @DisplayName("Add an item into shopping cart should throw exception when the email is invalid")
    void addCartItem_InvalidEmail_ShouldReturnException() {
        //Given
        BookDto bookDto = mapBookToDto(loadAllBooks().get(INITIAL_INDEX));
        CartItemRequestDto requestDto = createTestCartItemRequestDto();

        when(bookService.findById(requestDto.getBookId())).thenReturn(bookDto);
        when(shoppingCartsRepository.findByUserEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addCartItem(requestDto, INVALID_EMAIL));

        //INVALID_ID
        String expected = ERROR_MESSAGE_CART_NOT_FOUND + INVALID_EMAIL;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(bookService).findById(requestDto.getBookId());
        verify(shoppingCartsRepository).findByUserEmail(INVALID_EMAIL);
        verifyNoMoreInteractions(bookService, shoppingCartsRepository);
    }

    @Test
    @DisplayName("Get by user email should return ShoppingCart when a valid email is provided")
    void getShoppingCartByUserEmail_ValidEmail_ShouldReturnShoppingCart() {
        //Given
        User user = createTestUser(TEST_USER_ID_TWO, TEST_USER_ID_TWO_EMAIL);
        ShoppingCart expected = createTestShoppingCart(user);

        when(shoppingCartsRepository.findByUserEmail(user.getEmail()))
                .thenReturn(Optional.of(expected));

        //When
        ShoppingCart actual = shoppingCartService.getShoppingCartByUserEmail(user.getEmail());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(shoppingCartsRepository).findByUserEmail(user.getEmail());
        verifyNoMoreInteractions(shoppingCartsRepository);
    }

    @Test
    @DisplayName("Get by user email should throw exception when the email is invalid")
    void getShoppingCartByUserEmail_InvalidEmail_ShouldReturnException() {
        //Given
        when(shoppingCartsRepository.findByUserEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCartByUserEmail(INVALID_EMAIL));

        //INVALID_ID
        String expected = ERROR_MESSAGE_CART_NOT_FOUND + INVALID_EMAIL;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(shoppingCartsRepository).findByUserEmail(INVALID_EMAIL);
        verifyNoMoreInteractions(shoppingCartsRepository);
    }

    @Test
    @DisplayName("Clear shopping cart when a valid data is provided")
    void clearShoppingCart_ValidData_SuccessfullyClearedShoppingCart() {
        //Given
        BookDto bookDto = mapBookToDto(loadAllBooks().get(INITIAL_INDEX));
        ShoppingCart cart = initializeTestShoppingCart();

        CartItem cartItem = createTestCartItem(bookDto, BOOK_QUANTITY, cart);
        cartItem.setId(SAMPLE_TEST_ID);
        cart.setCartItems(new HashSet<>(Set.of(cartItem)));

        // When
        shoppingCartService.clearShoppingCart(cart);

        // Then
        assertTrue(cart.getCartItems().isEmpty(), SHOULD_BE_EMPTY_AFTER_CLEARING_THE_SHOPPING_CART);
        verify(shoppingCartsRepository).save(cart);
        verifyNoMoreInteractions(shoppingCartsRepository);
    }

    @Test
    @DisplayName("Update cart item successfully when valid data is provided")
    void updateCartItem_ValidData_ShouldReturnCartItemResponseDto() {
        //Given
        BookDto bookDto = mapBookToDto(loadAllBooks().get(INITIAL_INDEX));
        ShoppingCart cart = initializeTestShoppingCart();

        CartItem cartItem = createTestCartItem(bookDto, BOOK_QUANTITY, cart);
        cartItem.setId(SAMPLE_TEST_ID);
        cart.setCartItems(new HashSet<>(Set.of(cartItem)));

        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(UPDATED_BOOK_QUANTITY);

        CartItemResponseDto expected = new CartItemResponseDto(
                cartItem.getId(),
                bookDto.getId(),
                bookDto.getTitle(),
                UPDATED_BOOK_QUANTITY
        );

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        doAnswer(invocation -> {
            CartItem updatedCartItem = invocation.getArgument(1);
            updatedCartItem.setQuantity(UPDATED_BOOK_QUANTITY);
            return null;
        }).when(cartItemMapper).updateCartItemQuantity(requestDto, cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        //When
        CartItemResponseDto actual = shoppingCartService
                .updateCartItem(cartItem.getId(), requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(cartItemRepository).findById(cartItem.getId());
        verify(cartItemMapper).updateCartItemQuantity(requestDto, cartItem);
        verify(cartItemRepository).save(cartItem);
        verify(cartItemMapper).toDto(cartItem);
        verifyNoMoreInteractions(cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("Update cart item should throw exception when the CartItemId is invalid")
    void updateCartItem_InvalidCartItemId_ShouldReturnException() {
        //Given
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(UPDATED_BOOK_QUANTITY);

        when(cartItemRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItem(INVALID_ID, requestDto));

        //Then
        String expected = ERROR_MESSAGE_CART_ITEM_NOT_FOUND + INVALID_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        verify(cartItemRepository).findById(INVALID_ID);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Delete cart item successfully when valid ID is provided")
    void deleteCartItem_ValidId_ShouldDeleteCartItem() {
        // Given
        doNothing().when(cartItemRepository).deleteById(SAMPLE_TEST_ID);

        // When
        assertDoesNotThrow(() -> shoppingCartService.deleteCartItem(SAMPLE_TEST_ID));

        // Then
        verify(cartItemRepository).deleteById(SAMPLE_TEST_ID);
        verifyNoMoreInteractions(cartItemRepository);
    }
}
