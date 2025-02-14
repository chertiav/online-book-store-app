package com.achdev.onlinebookstoreapp.controller;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACCESS_DENIED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.BOOKS_ENDPOINT;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CAN_T_UPDATE_CATEGORY_BY_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CATEGORIES_ENDPOINT;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EIGHTH_BOOK_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EMPTY_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_FIELD_NAME_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_CATEGORY_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_NAME_MANDATORY;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_CATEGORY_NAME;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PATH_SEPARATOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TIMESTAMP_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createCategoryDtoFromRequest;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createCategoryRequestDtoFromCategory;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createErrorDetailMap;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createErrorResponse;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createPageResponse;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createSampleCategoryRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllCategories;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapCategoryToDto;
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

import com.achdev.onlinebookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.achdev.onlinebookstoreapp.dto.category.CategoryDto;
import com.achdev.onlinebookstoreapp.dto.category.CreateCategoryRequestDto;
import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.page.PageResponse;
import com.achdev.onlinebookstoreapp.mapper.BookMapper;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.model.Category;
import com.achdev.onlinebookstoreapp.repository.category.CategoryRepository;
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
import org.springframework.web.context.WebApplicationContext;

@DisplayName("CategoryControllerTest Integration Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static List<Category> categories;
    private static List<Book> books;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;
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
        categories = loadAllCategories();
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
            executeSqlScripts(
                    connection,
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
            executeSqlScripts(
                    connection,
                    "database/books/categories/remove-all-from-book_categories-table.sql",
                    "database/category/remove-categories-from-categories-table.sql",
                    "database/books/remove-all-books-from-books-table.sql"
            );
        }
    }

    @WithMockUser(username = "user")
    @DisplayName("Get all categories")
    @Order(1)
    @Test
    void getAll_ValidPageable_ShouldReturnPageOfCategoryDto() throws Exception {
        //Given
        List<CategoryDto> categoryDtos = categories.stream()
                .map(TestUtil::mapCategoryToDto)
                .toList();
        PageResponse<CategoryDto> expected = createPageResponse(categoryDtos);

        //When
        MvcResult result = mockMvc.perform(get(CATEGORIES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<CategoryDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                CategoryDto.class
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
    @DisplayName("Get all categories should return forbidden")
    @Order(2)
    @Test
    void getAll_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(get(CATEGORIES_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
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
    @DisplayName("Get all books by categories Id")
    @Order(3)
    @Test
    void getBooksByCategoryId_ValidCategoryId_ShouldReturnPageOfBookDto() throws Exception {
        //Given
        long categoryId = 1L;
        List<BookDtoWithoutCategoryIds> booksByCategory = List.of(
                bookMapper.toDtoWithoutCategories(books.get(INITIAL_INDEX)),
                bookMapper.toDtoWithoutCategories(books.get(EIGHTH_BOOK_INDEX))
        );
        PageResponse<BookDtoWithoutCategoryIds> expected = createPageResponse(booksByCategory);

        //When
        MvcResult result = mockMvc
                .perform(get(CATEGORIES_ENDPOINT + PATH_SEPARATOR + categoryId
                        + BOOKS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<BookDtoWithoutCategoryIds> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                BookDtoWithoutCategoryIds.class
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
    @DisplayName("Get all books by categories should return forbidden")
    @Order(4)
    @Test
    void getBooksByCategoryId_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        long categoryId = 1L;
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(get(CATEGORIES_ENDPOINT + PATH_SEPARATOR + categoryId
                        + BOOKS_ENDPOINT)
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
    @DisplayName("Get category by Id")
    @Order(5)
    @Test
    void getCategoryById_ValidId_ShouldReturnCategoryDto() throws Exception {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CategoryDto expected = mapCategoryToDto(category);

        //When
        MvcResult result = mockMvc
                .perform(get(CATEGORIES_ENDPOINT + PATH_SEPARATOR + category.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                CategoryDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Get category by Id should return forbidden")
    @Order(6)
    @Test
    void getCategoryByI_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(get(CATEGORIES_ENDPOINT + PATH_SEPARATOR + category.getId())
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
    @DisplayName("Get category by Id should return not found")
    @Order(7)
    @Test
    void getCategoryById_InvalidId_ShouldReturnNotFound() throws Exception {
        //Given
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_CATEGORY_NOT_FOUND + INVALID_ID);

        //When
        MvcResult result = mockMvc
                .perform(get(CATEGORIES_ENDPOINT + PATH_SEPARATOR + INVALID_ID)
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create category when valid data is provided")
    @Order(8)
    @Test
    void createCategory_ValidData_ShouldReturnCategoryDto() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = createSampleCategoryRequestDto();
        CategoryDto expected = createCategoryDtoFromRequest(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(CATEGORIES_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        CategoryDto actual = mapMvcResultToObjectDto(result, objectMapper, CategoryDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, ID_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertNotNull(actual.getId(), ID_SHOULD_NOT_BE_NULL);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create category when valid data is provided")
    @Order(9)
    @Test
    void createCategory_InvalidData_ShouldReturnBadRequest() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = createSampleCategoryRequestDto();
        requestDto.setName(EMPTY_VALUE);

        Map<String, String> errorDetailDto = createErrorDetailMap(
                ERROR_FIELD_NAME_VALUE,
                ERROR_MESSAGE_NAME_MANDATORY
        );
        CommonApiErrorResponse expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(CATEGORIES_ENDPOINT)
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
    @DisplayName("Create category should return forbidden")
    @Order(10)
    @Test
    void createCategory_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = createSampleCategoryRequestDto();
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(post(CATEGORIES_ENDPOINT)
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
    @DisplayName("Update category valid data is provided")
    @Order(11)
    @Test
    void updateCategory_ValidData_ShouldReturnCategoryDto() throws Exception {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoFromCategory(
                NEW_CATEGORY_NAME,
                category
        );

        CategoryDto expected = mapCategoryToDto(category);
        expected.setName(NEW_CATEGORY_NAME);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CATEGORIES_ENDPOINT + PATH_SEPARATOR + category.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CategoryDto actual = mapMvcResultToObjectDto(result, objectMapper, CategoryDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update category when invalid data is provided should return bad request")
    @Order(12)
    @Test
    void updateCategory_InvalidData_ShouldReturnBadRequest() throws Exception {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoFromCategory(
                EMPTY_VALUE,
                category
        );
        Map<String, String> errorDetailDto = createErrorDetailMap(
                ERROR_FIELD_NAME_VALUE,
                ERROR_MESSAGE_NAME_MANDATORY
        );
        CommonApiErrorResponse expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CATEGORIES_ENDPOINT + PATH_SEPARATOR + category.getId())
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
    @DisplayName("Update category when invalid categoryId is provided should return not found")
    @Order(13)
    @Test
    void updateCategory_InvalidId_ShouldReturnNotFound() throws Exception {
        //Given
        CreateCategoryRequestDto requestDto = createSampleCategoryRequestDto();
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.NOT_FOUND,
                CAN_T_UPDATE_CATEGORY_BY_ID + INVALID_ID);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CATEGORIES_ENDPOINT + PATH_SEPARATOR + INVALID_ID)
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
    @DisplayName("Update category should return forbidden")
    @Order(14)
    @Test
    void updateCategory_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CreateCategoryRequestDto requestDto = createSampleCategoryRequestDto();
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(put(CATEGORIES_ENDPOINT + PATH_SEPARATOR + category.getId())
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
    @DisplayName("Delete category when valid data is provided")
    @Order(15)
    @Test
    void deleteCategory_ValidData_ShouldReturnNoContent() throws Exception {
        //Given
        Category category = categories.get(INITIAL_INDEX);

        //When
        mockMvc.perform(delete(CATEGORIES_ENDPOINT + PATH_SEPARATOR + category.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        assertFalse(categoryRepository.existsById(category.getId()),
                OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION);
    }

    @WithMockUser(username = "user")
    @DisplayName("Delete category when should return forbidden")
    @Order(16)
    @Test
    void deleteCategory_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CommonApiErrorResponse expected = createErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED);

        //When
        MvcResult result = mockMvc
                .perform(delete(CATEGORIES_ENDPOINT + PATH_SEPARATOR + category.getId())
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
