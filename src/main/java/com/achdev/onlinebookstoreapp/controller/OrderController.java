package com.achdev.onlinebookstoreapp.controller;

import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.order.OrderDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderRequestDto;
import com.achdev.onlinebookstoreapp.dto.order.OrderStatusRequestDto;
import com.achdev.onlinebookstoreapp.dto.page.PageResponse;
import com.achdev.onlinebookstoreapp.service.OrderService;
import com.achdev.onlinebookstoreapp.util.ApiResponseConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Placing an order",
            description = "Create an order",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED,
                            description = "Successfully placed an order"
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_FORBIDDEN,
                            description = ApiResponseConstants.FORBIDDEN_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public OrderDto placeOrder(
            Authentication authentication,
            @RequestBody @Valid OrderRequestDto requestDto
    ) {
        return orderService.completeOrder(authentication.getName(), requestDto);
    }

    @Operation(
            summary = "Get all user's orders",
            description = "Retrieving a paginated list of all user's order history",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved list of user's order history"
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_FORBIDDEN,
                            description = ApiResponseConstants.FORBIDDEN_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public PageResponse<OrderDto> getAll(Authentication authentication,
                                         @ParameterObject Pageable pageable) {
        return PageResponse.of(orderService.findAllOrdersByUserEmail(
                authentication.getName(), pageable));
    }

    @Operation(
            summary = "Update order's status by ID",
            description = "Updating order status by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully updated order status"
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                            description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_FORBIDDEN,
                            description = ApiResponseConstants.FORBIDDEN_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateStatus(@PathVariable Long id,
                                 @RequestBody OrderStatusRequestDto requestDto) {
        return orderService.updateStatusById(id, requestDto);
    }
}
