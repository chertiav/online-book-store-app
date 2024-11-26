package com.achdev.onlinebookstoreapp.controller;

import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.book.BookSearchParameters;
import com.achdev.onlinebookstoreapp.dto.book.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.dto.errors.BookApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.page.PageResponse;
import com.achdev.onlinebookstoreapp.service.BookService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private static final String RESPONSE_CODE_OK = "200";
    private static final String RESPONSE_CODE_CREATED = "201";
    private static final String RESPONSE_CODE_BAD_REQUEST = "400";
    private static final String RESPONSE_CODE_NOT_FOUND = "404";
    private final BookService bookService;

    @Operation(
            summary = "Get all books",
            description = "Retrieve a paginated list of all books",
            responses = @ApiResponse(
                    responseCode = RESPONSE_CODE_OK,
                    description = "Successfully retrieved list of books"
            )
    )
    @GetMapping
    public PageResponse<BookDto> getAll(Pageable pageable) {
        Page<BookDto> page = bookService.findAll(pageable);
        return PageResponse.of(page);
    }

    @Operation(
            summary = "Get book by ID",
            description = "Retrieve a book by ID",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_OK,
                            description = "Successfully retrieved book information"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUND,
                            description = "Book not found",
                            content = @Content(schema = @Schema(
                                    implementation = BookApiErrorResponse.class))
                    ),
            }
    )
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(
            summary = "Search books by parameters",
            description = "Retrieve a paginated list of all searched books by parameters",
            responses = @ApiResponse(
                    responseCode = RESPONSE_CODE_OK,
                    description = "Successfully retrieved a paginated list of books"
            )
    )
    @GetMapping("/search")
    public PageResponse<BookDto> searchBooks(BookSearchParameters searchParameters,
                                             Pageable pageable) {
        Page<BookDto> page = bookService.search(searchParameters, pageable);
        return PageResponse.of(page);
    }

    @Operation(
            summary = "Create a new book",
            description = "Create a new book",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_CREATED,
                            description = "Successfully created a new book"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST,
                            description = "Invalid request",
                            content = @Content(schema = @Schema(
                                    implementation = BookApiErrorResponse.class))
                    ),
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @Operation(
            summary = "Update a book by Id",
            description = "Update a book by Id",
            responses = {
                    @ApiResponse(
                            responseCode = RESPONSE_CODE_OK,
                            description = "Successfully updated book information"
                    ),
                    @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUND,
                            description = "Book not found",
                            content = @Content(schema = @Schema(implementation =
                                    BookApiErrorResponse.class))
                    ),
            }
    )
    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.updateById(id, requestDto);
    }

    @Operation(
            summary = "Delete a book by Id",
            description = "Delete a book by Id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
