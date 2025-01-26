package com.achdev.onlinebookstoreapp.controller;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACCESS_DENIED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.AUTHOR_SEARCH_PARAMETER;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.BOOKS_ENDPOINT;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CAN_T_UPDATE_BOOK_BY_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EMPTY_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_FIELD_TITLE_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_BOOK_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_TITLE_MANDATORY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PATH_SEPARATOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SEARCH_ENDPOINT;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TITLE_SEARCH_PARAMETER;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createBookRequestDtoFromBook;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createErrorDetailMap;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createErrorResponse;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createPageResponse;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createSampleBookRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.mapMvcResultToObjectDto;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.parseErrorResponseFromMvcResult;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.parseObjectDtoPageResponse;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.scaleBookPrices;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.validateObjectDto;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.verifyErrorResponseEquality;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.verifyPageResponseMatch;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.verifyResponseEqualityWithExpected;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.book.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.page.PageResponse;
import com.achdev.onlinebookstoreapp.mapper.BookMapper;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.book.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("BookControllerTest Integration Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    private static List<Book> books;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;

    @BeforeAll
    static void setUp(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        setupDatabase(dataSource);
        books = loadAllBooks();
        scaleBookPrices(books);
    }

    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/books/add-books-to-books-table.sql",
                    "database/category/add-categories-to-categories-table.sql",
                    "database/books/categories/add-books_categories-to_books_categories-table.sql"
            );
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/books/categories/remove-all-from-book_categories-table.sql",
                    "database/category/remove-categories-from-categories-table.sql",
                    "database/books/remove-all-books-from-books-table.sql"
            );
        }
    }

    @WithMockUser(username = "user")
    @DisplayName("Get all books")
    @Order(1)
    @Test
    void getAll_ValidPageable_ShouldReturnPageOfBookDto() throws Exception {
        //Given
        List<BookDto> bookDtos = books.stream()
                .map(this::mapBookToBookDto)
                .toList();
        PageResponse<BookDto> expected = createPageResponse(bookDtos);

        //When
        MvcResult result = mockMvc.perform(get(BOOKS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<BookDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                BookDto.class
        );
        verifyPageResponseMatch(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Get all books should return forbidden")
    @Order(2)
    @Test
    void getAll_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKS_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "user")
    @DisplayName("Get book by Id")
    @Order(3)
    @Test
    void getBookById_ValidId_ShouldReturnBookDto() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        BookDto expected = mapBookToBookDto(book);

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKS_ENDPOINT + PATH_SEPARATOR + book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDto.class);
        validateObjectDto(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Get book by Id should return forbidden")
    @Order(4)
    @Test
    void getBookById_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKS_ENDPOINT + PATH_SEPARATOR + book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "user")
    @DisplayName("Get book by Id should return not found")
    @Order(5)
    @Test
    void getBookById_InvalidId_ShouldReturnNotFound() throws Exception {
        //Given
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_BOOK_NOT_FOUND + INVALID_ID);

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKS_ENDPOINT + PATH_SEPARATOR + INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "user")
    @DisplayName("Get book by all books by parameters")
    @Order(6)
    @Test
    void searchBooks_ValidParameters_ShouldReturnPageOfBook() throws Exception {
        //Given
        List<BookDto> bookDtos = List.of(mapBookToBookDto(books.get(INITIAL_INDEX)));
        PageResponse<BookDto> expected = createPageResponse(bookDtos);
        MultiValueMap<String, String> params = createSearchParams(
                FIRST_BOOK_TITLE,
                FIRST_BOOK_AUTHOR
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKS_ENDPOINT + PATH_SEPARATOR + SEARCH_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<BookDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                BookDto.class
        );
        verifyPageResponseMatch(actual, expected);
    }

    @WithMockUser(username = "user")
    @DisplayName("Get book by all books by parameters should return not found")
    @Order(7)
    @Test
    void searchBooks_InvalidParameters_ShouldReturnPageOfEmptyContent() throws Exception {
        //Given
        List<BookDto> bookDtos = List.of();
        PageResponse<BookDto> expected = createPageResponse(bookDtos);
        MultiValueMap<String, String> params = createSearchParams(INVALID_TITLE, INVALID_AUTHOR);

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKS_ENDPOINT + PATH_SEPARATOR + SEARCH_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<BookDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                BookDto.class
        );
        verifyPageResponseMatch(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Get book by all books by parameters should return forbidden")
    @Order(8)
    @Test
    void searchBooks_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);
        MultiValueMap<String, String> params = createSearchParams(
                FIRST_BOOK_TITLE,
                FIRST_BOOK_AUTHOR
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKS_ENDPOINT + PATH_SEPARATOR + SEARCH_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create book when valid data is provided")
    @Order(9)
    @Test
    void createBook_ValidData_ShouldReturnBookDto() throws Exception {
        //Given
        CreateBookRequestDto requestDto = createSampleBookRequestDto();
        BookDto expected = createBookDtoFromRequest(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(BOOKS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        BookDto actual = mapMvcResultToObjectDto(result, objectMapper, BookDto.class);
        verifyResponseEqualityWithExpected(expected, actual);
        assertNotNull(actual.getId(), ID_SHOULD_NOT_BE_NULL);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create book when invalid data is provided should return bad request")
    @Order(10)
    @Test
    void createBook_InvalidData_ShouldReturnBadRequest() throws Exception {
        //Given
        CreateBookRequestDto requestDto = createSampleBookRequestDto();
        requestDto.setTitle(EMPTY_VALUE);

        Map<String, String> errorDetailDto = createErrorDetailMap(
                ERROR_FIELD_TITLE_VALUE,
                ERROR_MESSAGE_TITLE_MANDATORY
        );
        CommonApiErrorResponse expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(BOOKS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "user")
    @DisplayName("Create book should return forbidden")
    @Order(11)
    @Test
    void createBook_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CreateBookRequestDto requestDto = createSampleBookRequestDto();
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(BOOKS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update book when valid data is provided")
    @Order(12)
    @Test
    void updateBook_ValidData_ShouldReturnBookDto() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CreateBookRequestDto requestDto = createBookRequestDtoFromBook(NEW_BOOK_TITLE, book);

        BookDto expected = mapBookToBookDto(book);
        expected.setTitle(NEW_BOOK_TITLE);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKS_ENDPOINT + PATH_SEPARATOR + book.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookDto actual = mapMvcResultToObjectDto(result, objectMapper, BookDto.class);
        validateObjectDto(actual, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update book when invalid data is provided should return bad request")
    @Order(13)
    @Test
    void updateBook_InvalidData_ShouldReturnBadRequest() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CreateBookRequestDto requestDto = createBookRequestDtoFromBook(EMPTY_VALUE, book);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                ERROR_FIELD_TITLE_VALUE,
                ERROR_MESSAGE_TITLE_MANDATORY
        );
        CommonApiErrorResponse expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKS_ENDPOINT + PATH_SEPARATOR + book.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update book when invalid bookId is provided should return not found")
    @Order(14)
    @Test
    void updateBook_InvalidId_ShouldReturnNotFound() throws Exception {
        //Given
        CreateBookRequestDto requestDto = createSampleBookRequestDto();
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.NOT_FOUND,
                CAN_T_UPDATE_BOOK_BY_ID + INVALID_ID);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKS_ENDPOINT + PATH_SEPARATOR + INVALID_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "user")
    @DisplayName("Update book should return forbidden")
    @Order(15)
    @Test
    void updateBook_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CreateBookRequestDto requestDto = createSampleBookRequestDto();
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKS_ENDPOINT + PATH_SEPARATOR + book.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete book when valid data is provided")
    @Order(16)
    @Test
    void deleteBook_ValidData_ShouldReturnNoContent() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);

        //When
        mockMvc.perform(delete(BOOKS_ENDPOINT + PATH_SEPARATOR + book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        assertFalse(bookRepository.existsById(book.getId()),
                OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION);
    }

    @WithMockUser(username = "user")
    @DisplayName("Delete book when should return forbidden")
    @Order(17)
    @Test
    void deleteBook_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(delete(BOOKS_ENDPOINT + PATH_SEPARATOR + book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponse actual = parseErrorResponseFromMvcResult(result, objectMapper);
        verifyErrorResponseEquality(actual, expected);
    }

    private BookDto mapBookToBookDto(Book book) {
        BookDto dto = bookMapper.toDto(book);
        dto.setPrice(dto.getPrice().setScale(2, RoundingMode.HALF_UP));
        return dto;
    }

    private MultiValueMap<String, String> createSearchParams(String title, String author) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(TITLE_SEARCH_PARAMETER, title);
        params.add(AUTHOR_SEARCH_PARAMETER, author);
        return params;
    }

    private BookDto createBookDtoFromRequest(CreateBookRequestDto requestDto) {
        BookDto expected = new BookDto();
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setDescription(requestDto.getDescription());
        expected.setCoverImage(requestDto.getCoverImage());
        expected.setCategoryIds(Set.of(1L, 2L));
        return expected;
    }
}
