package com.achdev.onlinebookstoreapp.utils;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_FIELD_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_BOOK_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_BOOK_COVER_IMAGE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_BOOK_DESCRIPTION;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_BOOK_ISBN;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_BOOK_PRICE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_CATEGORY_DESCRIPTION;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_CATEGORY_NAME;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TIMESTAMP_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.achdev.onlinebookstoreapp.dto.book.BookSearchParameters;
import com.achdev.onlinebookstoreapp.dto.book.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.dto.category.CategoryDto;
import com.achdev.onlinebookstoreapp.dto.category.CreateCategoryRequestDto;
import com.achdev.onlinebookstoreapp.dto.errors.CommonApiErrorResponse;
import com.achdev.onlinebookstoreapp.dto.page.PageResponse;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.model.Category;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MvcResult;

public final class TestHelper {
    private TestHelper() {
    }

    // ========================methods for generel usages===================================
    public static void executeSqlScripts(Connection connection, String... scripts) {
        for (String script : scripts) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(script));
        }
    }

    public static List<Book> loadAllBooks() {
        List<Category> categories = loadAllCategories();
        return List.of(
                createBook(
                        1L, "The Secret of the Forest", "Virginia Wolfe",
                        "1234567890", BigDecimal.valueOf(47.55),
                        "A thrilling story of adventure and mystery in the forest.",
                        "https://example.com/coverimage1.jpg",
                        createCategorySet(categories, 0, 4, 6)
                ),
                createBook(
                        2L, "Journey to the Unknown", "Michael Stevens",
                        "2345678901", BigDecimal.valueOf(59.99),
                        "An exciting journey to uncover unknown secrets.",
                        "https://example.com/coverimage2.jpg",
                        createCategorySet(categories, 1, 4, 9)
                ),
                createBook(
                        3L, "Mysteries of the Deep", "Harriet Potter",
                        "3456789012", BigDecimal.valueOf(39.9),
                        "A deep dive into the unknown depths of the ocean.",
                        "https://example.com/coverimage3.jpg",
                        createCategorySet(categories, 1, 19, 10)
                ),
                createBook(
                        4L, "Adventures in Flatland", "D. E. Abbott",
                        "4567890123", BigDecimal.valueOf(22.75),
                        "Adventures in a two-dimensional world.",
                        "https://example.com/coverimage4.jpg",
                        createCategorySet(categories, 19, 4, 12)
                ),
                createBook(
                        5L, "Galactic Tales", "K. L. Smith",
                        "6789012345", BigDecimal.valueOf(30.5),
                        "Tales of adventure across the galaxy.",
                        "https://example.com/coverimage6.jpg",
                        createCategorySet(categories, 3, 18, 6)
                ),
                createBook(
                        6L, "Whispering Shadows", "Robert Jordan",
                        "7890123456", BigDecimal.valueOf(45.2),
                        "A mystery shrouded in shadows.",
                        "https://example.com/coverimage7.jpg",
                        createCategorySet(categories, 16, 7, 10)
                ),
                createBook(
                        7L, "Magic and Mayhem", "J. K. Rowling",
                        "8901234567", BigDecimal.valueOf(38.99),
                        "A tale of magic and adventure.",
                        "https://example.com/coverimage8.jpg",
                        createCategorySet(categories, 8, 9, 13)
                ),
                createBook(
                        8L, "The Quantum Enigma", "Brian Greene",
                        "9012345678", BigDecimal.valueOf(55.45),
                        "Unraveling the mysteries of quantum physics.",
                        "https://example.com/coverimage9.jpg",
                        createCategorySet(categories, 17, 5, 14)
                ),
                createBook(
                        9L, "Dreams of Tomorrow", "Isaac Asimov",
                        "9123456789", BigDecimal.valueOf(36.85),
                        "A visionary look into the future.",
                        "https://example.com/coverimage10.jpg",
                        createCategorySet(categories, 0, 1, 2, 8)
                ),
                createBook(
                        10L, "Dreams of Tomorrow", "Isaac Asimov",
                        "9123456789а", BigDecimal.valueOf(1212.0),
                        "A visionary look into the future.",
                        "https://example.com/coverimage10.jpg",
                        createCategorySet(categories, 3, 11, 12)
                ),
                createBook(
                        11L, "The Last Whisper", "Unknown Author",
                        "184777106X", BigDecimal.valueOf(0.0),
                        "An unpublished script found in an ancient library, shrouded in mystery.",
                        "https://example.com/coverimage11.jpg",
                        createCategorySet(categories, 19)
                )
        );
    }

    public static List<Category> loadAllCategories() {
        return List.of(
                createCategory(1L, "Fantasy Adventure", "Fantasy Adventure"),
                createCategory(2L, "Science Fiction", "Science Fiction"),
                createCategory(3L, "Mystery Thriller", "Mystery Thriller"),
                createCategory(4L, "Historical Fiction", "Historical Fiction"),
                createCategory(5L, "Romance", "Romance"),
                createCategory(6L, "Horror", "Horror"),
                createCategory(7L, "Dystopian", "Dystopian"),
                createCategory(8L, "Young Adult", "Young Adult"),
                createCategory(9L, "Crime Drama", "Crime Drama"),
                createCategory(10L, "Action and Adventure", "Action and Adventure"),
                createCategory(11L, "Paranormal Romance", "Paranormal Romance"),
                createCategory(12L, "Steampunk", "Steampunk"),
                createCategory(13L, "Magical Realism", "Magical Realism"),
                createCategory(14L, "Epic Fantasy", "Epic Fantasy"),
                createCategory(15L, "Space Opera", "Space Opera"),
                createCategory(16L, "Contemporary Fiction", "Contemporary Fiction"),
                createCategory(17L, "Gothic Fiction", "Gothic Fiction"),
                createCategory(18L, "Cyberpunk", "Cyberpunk"),
                createCategory(19L, "Detective Mystery", "Detective Mystery"),
                createCategory(20L, "Psychological Thriller", "Psychological Thriller")
        );
    }

    public static void scaleBookPrices(List<Book> books) {
        books.forEach(book -> book.setPrice(
                book.getPrice().setScale(2, RoundingMode.HALF_UP)
        ));
    }

    //========================methods for controllers======================================
    public static <T> void verifyPageResponseMatch(
            PageResponse<T> actual,
            PageResponse<T> expected
    ) {
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

    public static <T> void validateObjectDto(T expected, T actual) {
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    public static Map<String, String> createErrorDetailMap(
            String errorFieldTitle,
            String errorMessageTitle
    ) {
        Map<String, String> errorDetailDto = new LinkedHashMap<>();
        errorDetailDto.put(ERROR_FIELD_TITLE, errorFieldTitle);
        errorDetailDto.put(ERROR_MESSAGE_TITLE, errorMessageTitle);
        return errorDetailDto;
    }

    public static <T> T mapMvcResultToObjectDto(
            MvcResult result,
            ObjectMapper objectMapper,
            Class<T> clazz
    ) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
    }

    public static <T> PageResponse<T> parseObjectDtoPageResponse(
            MvcResult result,
            ObjectMapper objectMapper,
            Class<T> clazz
    ) throws IOException {
        JavaType type = objectMapper
                .getTypeFactory()
                .constructParametricType(PageResponse.class, clazz);
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), type);

    }

    public static CommonApiErrorResponse parseErrorResponseFromMvcResult(
            MvcResult result,
            ObjectMapper objectMapper
    )
            throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CommonApiErrorResponse.class);
    }

    public static CommonApiErrorResponse createErrorResponse(HttpStatus status, Object message) {
        return new CommonApiErrorResponse(
                status,
                LocalDateTime.now(),
                message
        );
    }

    public static void verifyErrorResponseEquality(
            CommonApiErrorResponse actual,
            CommonApiErrorResponse expected
    ) {
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED);
        assertEquals(expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    public static <T> void verifyResponseEqualityWithExpected(T expected, T actual) {
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, ID_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    public static <T> PageResponse<T> createPageResponse(List<T> objectDtos) {
        Pageable pageable = PageRequest.of(0, 20);
        return PageResponse.of(new PageImpl<>(objectDtos, pageable, objectDtos.size()));
    }

    //========================methods for repositories======================================
    public static <T> void verifyPageContent(Page<T> expected, Page<T> actual) {
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(
                expected.getNumberOfElements(),
                actual.getNumberOfElements(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalPages(),
                actual.getTotalPages(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalElements(),
                actual.getTotalElements(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
    }

    public static Book createSampleBook() {
        Book book = new Book();
        book.setTitle(SAMPLE_BOOK_TITLE);
        book.setAuthor(SAMPLE_BOOK_AUTHOR);
        book.setIsbn(SAMPLE_BOOK_ISBN);
        book.setPrice(SAMPLE_BOOK_PRICE);
        book.setDescription(SAMPLE_BOOK_DESCRIPTION);
        book.setCoverImage(SAMPLE_BOOK_COVER_IMAGE);
        book.setDeleted(false);
        return book;
    }

    public static Category createSampleCategory() {
        Category category = new Category();
        category.setName(SAMPLE_CATEGORY_NAME);
        category.setDescription(SAMPLE_CATEGORY_DESCRIPTION);
        category.setDeleted(false);
        return category;
    }

    public static String getDeleteCheckMessage(String title, Long id) {
        return title + " with ID " + id + " should be found in the repository before deleting it";
    }

    public static String getNotFoundMessage(String title, Long id) {
        return title + " with ID " + id + " should not be found in the repository";
    }

    //========================methods for controllers======================================
    public static <T> Page<T> createPage(List<T> listOfObjects, Pageable pageable) {
        return new PageImpl<>(listOfObjects, pageable, listOfObjects.size());
    }

    public static BookSearchParameters createInvalidSearchParameters(String title, String author) {
        String[] titles = new String[]{title};
        String[] authors = new String[]{author};
        return new BookSearchParameters(titles, authors);
    }

    public static CreateBookRequestDto createSampleBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(SAMPLE_BOOK_TITLE);
        requestDto.setAuthor(SAMPLE_BOOK_AUTHOR);
        requestDto.setIsbn(SAMPLE_BOOK_ISBN);
        requestDto.setPrice(SAMPLE_BOOK_PRICE);
        requestDto.setDescription(SAMPLE_BOOK_DESCRIPTION);
        requestDto.setCoverImage(SAMPLE_BOOK_COVER_IMAGE);
        requestDto.setCategories(List.of(1L, 2L));
        return requestDto;
    }

    public static CreateBookRequestDto createBookRequestDtoFromBook(String newTitle, Book book) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(newTitle);
        requestDto.setAuthor(book.getAuthor());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setPrice(book.getPrice());
        requestDto.setDescription(book.getDescription());
        requestDto.setCoverImage(book.getCoverImage());
        requestDto.setCategories(List.of(1L, 5L, 7L));
        return requestDto;
    }

    public static CreateCategoryRequestDto createSampleCategoryRequestDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(SAMPLE_CATEGORY_NAME);
        requestDto.setDescription(SAMPLE_CATEGORY_DESCRIPTION);
        return requestDto;
    }

    public static CreateCategoryRequestDto createCategoryRequestDtoFromCategory(
            String newTitle,
            Category category
    ) {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(newTitle);
        requestDto.setDescription(category.getDescription());
        return requestDto;
    }

    public static CategoryDto mapCategoryToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        return categoryDto;
    }

    private static Book createBook(
            Long id, String title, String author, String isbn, BigDecimal price,
            String description, String coverImage, Set<Category> categories
    ) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setDescription(description);
        book.setCoverImage(coverImage);
        book.setDeleted(false);
        book.setCategories(categories);
        return book;
    }

    private static Category createCategory(Long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setDeleted(false);
        return category;
    }

    private static Set<Category> createCategorySet(
            List<Category> categories,
            Integer... indices
    ) {
        Set<Category> categorySet = new HashSet<>();
        for (int index : indices) {
            Category category = categories.get(index);
            if (category != null) {
                categorySet.add(category);
            }
        }
        return categorySet;
    }
}
