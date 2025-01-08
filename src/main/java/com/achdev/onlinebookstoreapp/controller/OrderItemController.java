package com.achdev.onlinebookstoreapp.controller;

import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.order.item.OrderItemDto;
import com.achdev.onlinebookstoreapp.dto.page.PageResponse;
import com.achdev.onlinebookstoreapp.service.OrderItemService;
import com.achdev.onlinebookstoreapp.util.ApiResponseConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order item management", description = "Endpoints for managing order items")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Operation(
            summary = "Get all order items for a specific order",
            description = "Retrieving a paginated list of all order items for a specific order",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved list of all order items for "
                                    + "a specific order"
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(implementation =
                                    CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_FORBIDDEN,
                            description = ApiResponseConstants.FORBIDDEN_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    public PageResponse<OrderItemDto> getAllOrderItems(
            Authentication authentication,
            @PathVariable Long orderId,
            @ParameterObject Pageable pageable
    ) {
        return PageResponse.of(orderItemService.findAllOrderItemsByOrderIdAndOrderUserEmail(
                orderId, authentication.getName(), pageable));
    }

    @Operation(
            summary = "Get a specific order item from an order",
            description = "Retrieving a specific order item from an order",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved a specific order item "
                                    + "from an order"
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(implementation =
                                    CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_FORBIDDEN,
                            description = ApiResponseConstants.FORBIDDEN_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemById(
            Authentication authentication,
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        return orderItemService.findOrderItemByIdAndOrderIdAndOrderUserEmail(
                itemId, orderId, authentication.getName());
    }
}
