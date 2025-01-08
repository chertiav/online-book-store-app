package com.achdev.onlinebookstoreapp.dto.order;

import static com.achdev.onlinebookstoreapp.model.Order.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request for updating the order status")
public class OrderStatusRequestDto {
    @NotNull(message = "Order status must not be null")
    @Schema(description = "Order status to be updated",
            example = "DELIVERED")
    private Status status;
}
