package com.achdev.onlinebookstoreapp.dto.cart.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateCartItemRequestDto extends CartItemBaseRequestDto {
}
