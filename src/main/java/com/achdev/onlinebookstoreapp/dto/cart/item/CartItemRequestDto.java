package com.achdev.onlinebookstoreapp.dto.cart.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequestDto {
    @NotNull(message = "Book ID cannot be null")
    @Positive(message = "Book ID must be a positive number")
    private Long bookId;
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}
