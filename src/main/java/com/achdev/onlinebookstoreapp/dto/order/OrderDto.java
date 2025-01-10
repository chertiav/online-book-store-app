package com.achdev.onlinebookstoreapp.dto.order;

import com.achdev.onlinebookstoreapp.dto.order.item.OrderItemDto;
import com.achdev.onlinebookstoreapp.model.Order.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Status status;
}
