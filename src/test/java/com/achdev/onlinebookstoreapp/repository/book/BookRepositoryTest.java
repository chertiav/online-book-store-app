package com.achdev.onlinebookstoreapp.repository.book;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EIGHTH_BOOK_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_TEST_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SEARCH_KEY_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SEARCH_KEY_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createEqualSpecification;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createSampleBook;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getDeleteCheckMessage;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getNotFoundMessage;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.scaleBookPrices;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.verifyPageContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.achdev.onlinebookstoreapp.model.Book;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@DisplayName("BookRepository Integration Test")
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private static final long TEST_NEW_BOOK_ID = 12L;
    private static final String BOOK = "Book";
    private static List<Book> books;
    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
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

    @Order(1)
    @Test
    @DisplayName("Find all books")
    void findAll_ValidPageable_ShouldReturnPageOfBook() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> expected = new PageImpl<>(books, pageable, books.size());

        //When
        Page<Book> actual = bookRepository.findAll(pageable);

        //Then
        verifyPageContent(actual, expected);
    }

    @Order(2)
    @Test
    @DisplayName("Find all books on the second page")
    void findAll_ValidPageable_ShouldReturnSecondPageOfBook() {
        // Given
        List<Book> booksOnSecondPage = books.subList(5, 10);
        Pageable pageable = PageRequest.of(1, 5);
        Page<Book> expected = new PageImpl<>(booksOnSecondPage, pageable, books.size());

        // When
        Page<Book> actual = bookRepository.findAll(pageable);

        // Then
        verifyPageContent(actual, expected);
    }

    @Order(3)
    @DisplayName("Find all books by categories Id")
    @Test
    void findAllByCategories_ValidCategoryId_ShouldReturnPageOfBook() {
        //Given
        Long categoryId = 1L;
        List<Book> booksByCategory = List.of(
                books.get(INITIAL_INDEX),
                books.get(EIGHTH_BOOK_INDEX)
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> expected = new PageImpl<>(booksByCategory, pageable, booksByCategory.size());

        //When
        Page<Book> actual = bookRepository.findAllByCategories(categoryId, pageable);

        //Then
        verifyPageContent(actual, expected);
    }

    @Order(4)
    @Test
    @DisplayName("Find book by Id")
    void findById_ValidId_ShouldReturnBook() {
        //Given
        Book expected = books.get(INITIAL_INDEX);

        //When
        Optional<Book> actual = bookRepository.findById(expected.getId());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Order(5)
    @Test
    @DisplayName("Find all books by parameters")
    void search_ValidParameters_ShouldReturnPageOfBook() {
        //Given
        Specification<Book> specification = Specification
                .where(createEqualSpecification(SEARCH_KEY_TITLE, FIRST_BOOK_TITLE))
                .and(createEqualSpecification(SEARCH_KEY_AUTHOR, FIRST_BOOK_AUTHOR));
        List<Book> selectedBooks = List.of(books.get(INITIAL_INDEX));
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> expected = new PageImpl<>(selectedBooks, pageable, selectedBooks.size());

        //When
        Page<Book> actual = bookRepository.findAll(specification, pageable);

        //Then
        verifyPageContent(actual, expected);
    }

    @Order(6)
    @ParameterizedTest
    @MethodSource("provideSearchParameters")
    @DisplayName("Should return empty page for invalid search parameters")
    void search_InvalidParameters_ShouldReturnEmptyPage(String searchKey, String searchValue) {
        // Given
        Specification<Book> specification = Specification
                .where(createEqualSpecification(searchKey, searchValue));
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> expected = new PageImpl<>(List.of(), pageable, 0);

        // When
        Page<Book> actual = bookRepository.findAll(specification, pageable);

        // Then
        verifyPageContent(actual, expected);
    }

    @Order(7)
    @Test
    @DisplayName("Save book successfully when valid data is provided")
    void save_ValidData_ShouldReturnBook() {
        //Given
        Book newBook = createSampleBook();

        Book expected = createSampleBook();
        expected.setId(TEST_NEW_BOOK_ID);

        //When
        Book actual = bookRepository.save(newBook);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, ID_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertNotNull(actual.getId());
    }

    @Order(8)
    @Test
    @DisplayName("Delete book successfully when valid ID is provided")
    void deleteById_ValidId_ShouldDeleteBook() {
        //Given
        Optional<Book> bookResultBefore = bookRepository.findById(SAMPLE_TEST_ID);

        // When
        bookRepository.deleteById(SAMPLE_TEST_ID);

        // Then
        Optional<Book> bookResultAfter = bookRepository.findById(SAMPLE_TEST_ID);

        assertTrue(bookResultBefore.isPresent(), getDeleteCheckMessage(BOOK, SAMPLE_TEST_ID));
        assertTrue(bookResultAfter.isEmpty(), getNotFoundMessage(BOOK, SAMPLE_TEST_ID));
    }

    @Order(9)
    @Test
    @DisplayName("Should not delete book when invalid ID is provided")
    void deleteById_InvalidId_ShouldNotDeleteAnyBook() {
        // Given
        Optional<Book> bookResultBefore = bookRepository.findById(INVALID_ID);

        // When
        bookRepository.deleteById(INVALID_ID);

        // Then
        Optional<Book> bookResultAfter = bookRepository.findById(INVALID_ID);

        assertTrue(bookResultBefore.isEmpty(), getNotFoundMessage(BOOK, INVALID_ID));
        assertTrue(bookResultAfter.isEmpty(), getNotFoundMessage(BOOK, INVALID_ID));
    }

    private static Stream<Arguments> provideSearchParameters() {
        return Stream.of(
                Arguments.of(SEARCH_KEY_TITLE, INVALID_TITLE),
                Arguments.of(SEARCH_KEY_AUTHOR, INVALID_AUTHOR)
        );
    }
}
