package com.achdev.onlinebookstoreapp.service.impl;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_BOOK_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_TEST_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.bookFromRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createBookRequestDtoFromBook;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createInvalidSearchParameters;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createPage;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.createSampleBookRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.getBookSpecification;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapBookToBookDtoWithoutCategoryIds;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.mapBookToDto;
import static com.achdev.onlinebookstoreapp.utils.TestUtil.scaleBookPrices;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.achdev.onlinebookstoreapp.dto.book.BookDto;
import com.achdev.onlinebookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.achdev.onlinebookstoreapp.dto.book.BookSearchParameters;
import com.achdev.onlinebookstoreapp.dto.book.CreateBookRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.BookMapper;
import com.achdev.onlinebookstoreapp.model.Book;
import com.achdev.onlinebookstoreapp.repository.book.BookRepository;
import com.achdev.onlinebookstoreapp.repository.book.BookSpecificationBuilder;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Service Implementation Test")
class BookServiceImplTest {
    private static List<Book> books;
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @BeforeAll
    static void setUp() {
        books = loadAllBooks();
        scaleBookPrices(books);
    }

    @Test
    @DisplayName("Find all books")
    void findAll_ValidPageable_ShouldReturnPageOfBookDto() {
        //Given
        Book book = books.get(INITIAL_INDEX);
        BookDto bookDto = mapBookToDto(book);
        Pageable pageable = PageRequest.of(0, 20);

        Page<Book> bookPage = createPage(List.of(book), pageable);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        Page<BookDto> actual = bookService.findAll(pageable);

        //Then
        Page<BookDto> expected = createPage(List.of(bookDto), pageable);

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

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books when repository returns empty page")
    void findAll_WhenRepositoryReturnsEmptyPage_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> bookPage = createPage(List.of(), pageable);
        Page<BookDto> expected = createPage(List.of(), pageable);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        //When
        Page<BookDto> actual = bookService.findAll(pageable);

        //Then
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

        verify(bookRepository).findAll(pageable);
        verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("Find all books by category id")
    void findAllByCategoryId_ValidCategory_ShouldReturnPageOfBookDtoWithoutCategoryIds() {
        //Given
        long categoryId = SAMPLE_TEST_ID;
        Book book = books.get(INITIAL_INDEX);
        BookDtoWithoutCategoryIds bookDto = mapBookToBookDtoWithoutCategoryIds(book);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> bookPage = createPage(List.of(book), pageable);

        when(bookRepository.findAllByCategories(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDto);

        //When
        Page<BookDtoWithoutCategoryIds> actual = bookService
                .findAllByCategoryId(categoryId, pageable);

        //Then
        Page<BookDtoWithoutCategoryIds> expected = createPage(List.of(bookDto), pageable);

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

        verify(bookRepository).findAllByCategories(categoryId, pageable);
        verify(bookMapper).toDtoWithoutCategories(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find by Id should return BookDto when a valid ID is provided")
    void findById_ValidId_ShouldReturnBookDto() {
        //Given
        Book book = books.get(INITIAL_INDEX);
        BookDto expected = mapBookToDto(book);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        //When
        BookDto actual = bookService.findById(book.getId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookRepository).findById(book.getId());
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find by Id should throw exception when the ID is invalid")
    void findById_InvalidId_ShouldReturnException() {
        //Given
        when(bookRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(INVALID_ID));

        //Then
        String expected = ERROR_MESSAGE_BOOK_NOT_FOUND + INVALID_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookRepository).findById(INVALID_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Search books with given parameters and pagination")
    void search_ValidParameters_ShouldReturnPageOfBookDto() {
        //Given
        Book book = books.get(INITIAL_INDEX);
        BookDto bookDto = mapBookToDto(book);
        Pageable pageable = PageRequest.of(0, 20);

        BookSearchParameters searchParameters = createInvalidSearchParameters(
                FIRST_BOOK_TITLE,
                FIRST_BOOK_AUTHOR
        );
        Specification<Book> bookSpecification = getBookSpecification(
                FIRST_BOOK_TITLE,
                FIRST_BOOK_AUTHOR
        );

        Page<Book> bookPage = createPage(List.of(book), pageable);

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        Page<BookDto> actual = bookService.search(searchParameters, pageable);

        //Then
        Page<BookDto> expected = createPage(List.of(bookDto), pageable);

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

        verify(bookSpecificationBuilder).build(searchParameters);
        verify(bookRepository).findAll(bookSpecification, pageable);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Save book successfully when valid data is provided")
    void save_ValidData_ShouldReturnSavedBookDto() {
        //Given
        CreateBookRequestDto requestDto = createSampleBookRequestDto();
        Book bookModel = bookFromRequestDto(requestDto);

        Book book = bookFromRequestDto(requestDto);
        book.setId(SAMPLE_TEST_ID);

        BookDto expected = mapBookToDto(book);
        when(bookMapper.toModel(requestDto)).thenReturn(bookModel);
        when(bookRepository.save(bookModel)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //When
        BookDto actual = bookService.save(requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookMapper).toModel(requestDto);
        verify(bookRepository).save(bookModel);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update book successfully when valid data is provided")
    void updateById_ValidData_ShouldReturnUpdatedBookDto() {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CreateBookRequestDto requestDto = createBookRequestDtoFromBook(NEW_BOOK_TITLE, book);

        BookDto expected = mapBookToDto(book);
        expected.setTitle(NEW_BOOK_TITLE);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        doAnswer(invocation -> {
            Book updatedBook = invocation.getArgument(1);
            updatedBook.setTitle(NEW_BOOK_TITLE);
            return null;
        }).when(bookMapper).updateBookFromDto(requestDto, book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //When
        BookDto actual = bookService.updateById(book.getId(), requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(bookRepository).findById(book.getId());
        verify(bookMapper).updateBookFromDto(requestDto, book);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Should delete book successfully when valid ID is provided")
    void deleteById_ValidId_ShouldDeleteBook() {
        // Given
        doNothing().when(bookRepository).deleteById(SAMPLE_TEST_ID);

        // When
        assertDoesNotThrow(() -> bookService.deleteById(SAMPLE_TEST_ID));

        // Then
        verify(bookRepository).deleteById(SAMPLE_TEST_ID);
        verifyNoMoreInteractions(bookRepository);
    }
}
