package com.achdev.onlinebookstoreapp.utils;

import java.math.BigDecimal;

public final class TestConstants {
    // ======================== General Test Messages ========================
    public static final String TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE =
            "The total elements in the page do not match the expected value.";
    public static final String TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The total number of pages does not match the expected value.";
    public static final String PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The page size does not match the expected value.";
    public static final String CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The content of the page does not match the expected value.";
    public static final String ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE =
            "The actual result should be equal to the expected one";
    public static final String ACTUAL_RESULT_SHOULD_NOT_BE_NULL =
            "The actual result should not be null";
    public static final String ID_SHOULD_NOT_BE_NULL = "ID should not be null";
    public static final String EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE =
            "The exception message should be equal to the expected one";
    public static final String CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The current page does not match the expected value.";
    public static final String OBJECT_SHOULD_NO_LONGER_EXIST_AFTER_DELETION =
            "Object should no longer exist after deletion";
    public static final String ACTUAL_OBJECT_DOES_NOT_MATCH_THE_EXPECTED =
            "The actual object does not match the expected one, ignoring the 'timestamp' field";
    public static final String DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH =
            "The date part of the timestamp does not match between expected and actual objects";
    // ======================== Error Messages ========================
    public static final String ERROR_MESSAGE_CATEGORY_NOT_FOUND = "Can't find category by id: ";
    public static final String ERROR_MESSAGE_BOOK_NOT_FOUND = "Can't find book by id: ";
    public static final String ACCESS_DENIED = "Access Denied";
    public static final String ERROR_MESSAGE_TITLE_MANDATORY = "Title is mandatory";
    public static final String ERROR_MESSAGE_NAME_MANDATORY = "Name is mandatory";
    public static final String CAN_T_UPDATE_BOOK_BY_ID = "Can't update book by id: ";
    public static final String CAN_T_UPDATE_CATEGORY_BY_ID = "Can't update category by id: ";
    // ======================== Test Data ========================
    public static final long INVALID_ID = 100L;
    public static final long SAMPLE_TEST_ID = 1L;
    public static final int INITIAL_INDEX = 0;
    public static final int EIGHTH_BOOK_INDEX = 8;
    public static final String NEW_BOOK_TITLE = "New book title";
    public static final String NEW_CATEGORY_NAME = "New category name";
    public static final String FIRST_BOOK_TITLE = "The Secret of the Forest";
    public static final String FIRST_BOOK_AUTHOR = "Virginia Wolfe";
    public static final String SAMPLE_BOOK_TITLE = "New Adventures";
    public static final String SAMPLE_BOOK_AUTHOR = "John Smith";
    public static final String SAMPLE_BOOK_ISBN = "9988776655";
    public static final BigDecimal SAMPLE_BOOK_PRICE = BigDecimal.valueOf(25.99);
    public static final String SAMPLE_BOOK_DESCRIPTION =
            "An exciting new story of adventures like never before.";
    public static final String SAMPLE_BOOK_COVER_IMAGE = "https://example.com/coverimage11.jpg";
    public static final String SAMPLE_CATEGORY_NAME = "New Category";
    public static final String SAMPLE_CATEGORY_DESCRIPTION = "Description for new category";
    public static final String EMPTY_VALUE = "";
    public static final String INVALID_TITLE = "Invalid Title";
    public static final String INVALID_AUTHOR = "Invalid Author";
    public static final String ID_FIELD = "id";
    // ======================== API Endpoints ========================
    public static final String PATH_SEPARATOR = "/";
    public static final String CATEGORIES_ENDPOINT = "/categories";
    public static final String BOOKS_ENDPOINT = "/books";
    public static final String SEARCH_ENDPOINT = "search";
    // ======================== Error Field Keys ========================
    public static final String ERROR_FIELD_TITLE = "field";
    public static final String ERROR_FIELD_TITLE_VALUE = "title";
    public static final String ERROR_FIELD_NAME_VALUE = "name";
    public static final String ERROR_MESSAGE_TITLE = "message";
    public static final String TIMESTAMP_FIELD = "timestamp";
    // ======================== Search Parameters ========================
    public static final String TITLE_SEARCH_PARAMETER = "titles";
    public static final String AUTHOR_SEARCH_PARAMETER = "authors";
    public static final String SEARCH_KEY_TITLE = "title";
    public static final String SEARCH_KEY_AUTHOR = "author";

    private TestConstants() {
    }
}
