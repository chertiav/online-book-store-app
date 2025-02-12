package com.achdev.onlinebookstoreapp.repository.category;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ID_FIELD;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_TEST_ID;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createSampleCategory;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.executeSqlScripts;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getDeleteCheckMessage;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getNotFoundMessage;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllCategories;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.verifyPageContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.achdev.onlinebookstoreapp.model.Category;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("CategoryRepository Integration Test")
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    public static final long TEST_CATEGORY_ID = 21L;
    private static final String CATEGORY = "Category";
    private static List<Category> categories;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        setupDatabase(dataSource);
        categories = loadAllCategories();
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
                    "database/category/add-categories-to-categories-table.sql"
            );
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(
                    connection,
                    "database/category/remove-categories-from-categories-table.sql"
            );
        }
    }

    @Order(1)
    @Test
    @DisplayName("Find all categories")
    void findAll_ValidPageable_ShouldReturnPageOfCategory() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Category> expected = new PageImpl<>(categories, pageable, categories.size());

        //When
        Page<Category> actual = categoryRepository.findAll(pageable);

        //Then
        verifyPageContent(actual, expected);
    }

    @Order(2)
    @Test
    @DisplayName("Find all categories on the second page")
    void findAll_ValidPageable_ShouldReturnSecondPageOfCategory() {
        // Given
        List<Category> booksOnSecondPage = categories.subList(5, 10);
        Pageable pageable = PageRequest.of(1, 5);
        Page<Category> expected = new PageImpl<>(booksOnSecondPage, pageable, categories.size());

        // When
        Page<Category> actual = categoryRepository.findAll(pageable);

        // Then
        verifyPageContent(actual, expected);
    }

    @Order(3)
    @Test
    @DisplayName("Find category by Id")
    void findById_ValidId_ShouldReturnCategory() {
        //Given
        Category expected = categories.get(INITIAL_INDEX);

        //When
        Optional<Category> actual = categoryRepository.findById(expected.getId());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Order(4)
    @Test
    @DisplayName("Save category successfully when valid data is provided")
    void save_ValidData_ShouldReturnCategory() {
        //Given
        Category newCategory = createSampleCategory();

        Category expected = createSampleCategory();
        expected.setId(TEST_CATEGORY_ID);

        //When
        Category actual = categoryRepository.save(newCategory);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(reflectionEquals(expected, actual, ID_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertNotNull(actual.getId());
    }

    @Order(5)
    @Test
    @DisplayName("Delete category successfully when valid ID is provided")
    void deleteById_ValidId_ShouldDeleteCategory() {
        //Given
        Optional<Category> categoryResultBefore = categoryRepository.findById(SAMPLE_TEST_ID);

        // When
        categoryRepository.deleteById(SAMPLE_TEST_ID);

        // Then
        Optional<Category> categoryResultAfter = categoryRepository.findById(SAMPLE_TEST_ID);

        assertTrue(categoryResultBefore.isPresent(),
                getDeleteCheckMessage(CATEGORY, SAMPLE_TEST_ID));
        assertTrue(categoryResultAfter.isEmpty(), getNotFoundMessage(CATEGORY, SAMPLE_TEST_ID));
    }

    @Order(6)
    @Test
    @DisplayName("Should not category when invalid ID is provided")
    void deleteById_InvalidId_ShouldNotDeleteAnyCategory() {
        //Given
        Optional<Category> categoryResultBefore = categoryRepository.findById(INVALID_ID);

        // When
        categoryRepository.deleteById(INVALID_ID);

        // Then
        Optional<Category> categoryResultAfter = categoryRepository.findById(INVALID_ID);

        assertTrue(categoryResultBefore.isEmpty(), getNotFoundMessage(CATEGORY, INVALID_ID));
        assertTrue(categoryResultAfter.isEmpty(), getNotFoundMessage(CATEGORY, INVALID_ID));
    }
}
