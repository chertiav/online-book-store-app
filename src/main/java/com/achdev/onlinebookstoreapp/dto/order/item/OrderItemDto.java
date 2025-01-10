package com.achdev.onlinebookstoreapp.dto.order.item;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
