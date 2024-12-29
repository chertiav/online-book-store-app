package com.achdev.onlinebookstoreapp.dto.cart.item;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity
) {
}
