package com.achdev.onlinebookstoreapp.dto.cart.item;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public abstract class CartItemBaseRequestDto {
    @NotNull(message = "Quantity cannot be null")
    @Digits(integer = 6, fraction = 0, message = "Quantity should be a valid integer number")
    @Min(value = 0, message = "Quantity must be 0 or greater than zero")
    private int quantity;
}
