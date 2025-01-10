package com.achdev.onlinebookstoreapp.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequestDto {
    @NotBlank(message = "The shipping address is mandatory")
    private String shippingAddress;
}
