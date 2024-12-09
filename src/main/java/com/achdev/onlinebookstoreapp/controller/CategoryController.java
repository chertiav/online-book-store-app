package com.achdev.onlinebookstoreapp.controller;

import com.achdev.onlinebookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.achdev.onlinebookstoreapp.dto.category.CategoryDto;
import com.achdev.onlinebookstoreapp.dto.category.CreateCategoryRequestDto;
import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.page.PageResponse;
import com.achdev.onlinebookstoreapp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Categories management", description = "Endpoints for managing categories")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private static final String RESPONSE_CODE_OK = "200";
    private static final String RESPONSE_CODE_CREATED = "201";
    private static final String RESPONSE_CODE_BAD_REQUEST = "400";
    private static final String RESPONSE_CODE_FORBIDDEN = "403";
    private static final String RESPONSE_CODE_NOT_FOUND = "404";
    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Retrieve a paginated list of all categories",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_OK,
                            description = "Successfully retrieved list of categories"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,
                            description = "Access denied",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public PageResponse<CategoryDto> getAll(Pageable pageable) {
        Page<CategoryDto> page = categoryService.findAll(pageable);
        return PageResponse.of(page);
    }

    @Operation(
            summary = "Get books by category",
            description = "Retrieve a paginated list of books by category",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_OK,
                            description = "Successfully retrieved list of books by category"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,
                            description = "Access denied",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/books")
    public PageResponse<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @PathVariable Long id,
            Pageable pageable) {
        Page<BookDtoWithoutCategoryIds> page = categoryService
                .findAllBooksByCategoryId(id, pageable);
        return PageResponse.of(page);
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieve a category by ID",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_OK,
                            description = "Successfully retrieved category information"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUND,
                            description = "Category not found",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,
                            description = "Access denied",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @Operation(
            summary = "Create a new category",
            description = "Create a new category",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_CREATED,
                            description = "Successfully created a new category"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST,
                            description = "Invalid request",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,
                            description = "Access denied",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public CategoryDto createCategory(@RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @Operation(
            summary = "Update a category by Id",
            description = "Update a category by Id",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_OK,
                            description = "Successfully updated category information"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUND,
                            description = "Category not found",
                            content = @Content(schema = @Schema(implementation =
                                    CommonApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,
                            description = "Access denied",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.updateById(id, requestDto);
    }

    @Operation(
            summary = "Delete a category by Id",
            description = "Delete a category by Id",
            responses = {
                    @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,
                            description = "Access denied",
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponse.class))
                    )
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
