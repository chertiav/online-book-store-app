package com.achdev.onlinebookstoreapp.controller;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACCESS_DENIED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.BOOKS_ENDPOINT;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CAN_T_UPDATE_BOOK_BY_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EMPTY_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_FIELD_TITLE_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_BOOK_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_TITLE_MANDATORY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PATH_SEPARATOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SEARCH_ENDPOINT;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TIMESTAMP_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createBookDtoFromRequest;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createBookRequestDtoFromBook;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createErrorDetailMap;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createErrorResponse;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createPageResponse;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createSampleBookRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createSearchParams;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapBookToDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapMvcResultToObjectDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.parseErrorResponseFromMvcResult;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.parseObjectDtoPageResponse;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.scaleBookPrices;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.book.BookRepository;
import com.achdev.onlinebookstoreapp.utils.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
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
                    "database/books/categories/add-all-to_books_categories-table.sql"
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
                .map(TestUtil::mapBookToDto)
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalElementCount(),
                actual.getMetadata().getTotalElementCount(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalPageCount(),
                actual.getMetadata().getTotalPageCount(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getCurrentPage(),
                actual.getMetadata().getCurrentPage(),
                CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getPageSize(),
                actual.getMetadata().getPageSize(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithMockUser(username = "user")
    @DisplayName("Get book by Id")
    @Order(3)
    @Test
    void getBookById_ValidId_ShouldReturnBookDto() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        BookDto expected = mapBookToDto(book);

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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithMockUser(username = "user")
    @DisplayName("Get book by all books by parameters")
    @Order(6)
    @Test
    void searchBooks_ValidParameters_ShouldReturnPageOfBook() throws Exception {
        //Given
        List<BookDto> bookDtos = List.of(mapBookToDto(books.get(INITIAL_INDEX)));
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalElementCount(),
                actual.getMetadata().getTotalElementCount(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalPageCount(),
                actual.getMetadata().getTotalPageCount(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getCurrentPage(),
                actual.getMetadata().getCurrentPage(),
                CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getPageSize(),
                actual.getMetadata().getPageSize(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalElementCount(),
                actual.getMetadata().getTotalElementCount(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalPageCount(),
                actual.getMetadata().getTotalPageCount(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getCurrentPage(),
                actual.getMetadata().getCurrentPage(),
                CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getPageSize(),
                actual.getMetadata().getPageSize(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, ID_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update book when valid data is provided")
    @Order(12)
    @Test
    void updateBook_ValidData_ShouldReturnBookDto() throws Exception {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CreateBookRequestDto requestDto = createBookRequestDtoFromBook(NEW_BOOK_TITLE, book);

        BookDto expected = mapBookToDto(book);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
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

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }
}
