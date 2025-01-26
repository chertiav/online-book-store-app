package com.achdev.onlinebookstoreapp.service.impl;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_BOOK_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.FIRST_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_BOOK_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_TEST_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SEARCH_KEY_AUTHOR;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SEARCH_KEY_TITLE;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createBookRequestDtoFromBook;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createInvalidSearchParameters;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createPage;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createSampleBookRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.loadAllBooks;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.scaleBookPrices;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.validateObjectDto;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.verifyPageContent;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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
import com.achdev.onlinebookstoreapp.model.Category;
import com.achdev.onlinebookstoreapp.repository.book.BookRepository;
import com.achdev.onlinebookstoreapp.repository.book.BookSpecificationBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
        BookDto bookDto = mapToDto(book);
        Pageable pageable = PageRequest.of(0, 20);

        Page<Book> bookPage = createPage(List.of(book), pageable);
        Page<BookDto> expected = createPage(List.of(bookDto), pageable);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        Page<BookDto> actual = bookService.findAll(pageable);

        //Then
        verifyPageContent(expected, actual);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
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
        verifyPageContent(expected, actual);

        verify(bookRepository, times(1)).findAll(pageable);
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
        Page<BookDtoWithoutCategoryIds> expected = createPage(List.of(bookDto), pageable);

        when(bookRepository.findAllByCategories(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDto);

        //When
        Page<BookDtoWithoutCategoryIds> actual = bookService
                .findAllByCategoryId(categoryId, pageable);

        //Then
        verifyPageContent(expected, actual);

        verify(bookRepository, times(1))
                .findAllByCategories(categoryId, pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategories(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find by Id should return BookDto when a valid ID is provided")
    void findById_ValidId_ShouldReturnBookDto() {
        //Given
        Book book = books.get(INITIAL_INDEX);
        BookDto expected = mapToDto(book);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        //When
        BookDto actual = bookService.findById(book.getId());

        //Then
        validateObjectDto(expected, actual);

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookMapper, times(1)).toDto(book);
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

        verify(bookRepository, times(1)).findById(INVALID_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Search books with given parameters and pagination")
    void search_ValidParameters_ShouldReturnPageOfBookDto() {
        //Given
        Book book = books.get(INITIAL_INDEX);
        BookDto bookDto = mapToDto(book);
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
        verifyPageContent(expected, actual);

        verify(bookSpecificationBuilder, times(1)).build(searchParameters);
        verify(bookRepository, times(1)).findAll(bookSpecification, pageable);
        verify(bookMapper, times(1)).toDto(book);
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

        BookDto expected = mapToDto(book);
        when(bookMapper.toModel(requestDto)).thenReturn(bookModel);
        when(bookRepository.save(bookModel)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //When
        BookDto actual = bookService.save(requestDto);

        //Then
        validateObjectDto(expected, actual);

        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(bookModel);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update book successfully when valid data is provided")
    void updateById_ValidData_ShouldReturnUpdatedBookDto() {
        //Given
        Book book = books.get(INITIAL_INDEX);
        CreateBookRequestDto requestDto = createBookRequestDtoFromBook(NEW_BOOK_TITLE, book);

        BookDto expected = mapToDto(book);
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
        validateObjectDto(expected, actual);

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookMapper, times(1)).updateBookFromDto(requestDto, book);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
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
        verify(bookRepository, times(1)).deleteById(SAMPLE_TEST_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    private BookDto mapToDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(getCategoryIds(book.getCategories()));
        return bookDto;
    }

    private Set<Long> getCategoryIds(Set<Category> categories) {
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    private BookDtoWithoutCategoryIds mapBookToBookDtoWithoutCategoryIds(Book book) {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        return bookDto;
    }

    private Specification<Book> getBookSpecification(String title, String author) {
        return Specification
                .where(createEqualSpecification(SEARCH_KEY_TITLE, title))
                .and(createEqualSpecification(SEARCH_KEY_AUTHOR, author));
    }

    private Specification<Book> createEqualSpecification(String key, String value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(key), value);
    }

    private Book bookFromRequestDto(CreateBookRequestDto requestDto) {
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        book.setCategories(mapToCategories(requestDto));
        return book;
    }

    private Set<Category> mapToCategories(CreateBookRequestDto requestDto) {
        return requestDto.getCategories().stream()
                .map(this::getCategory)
                .collect(Collectors.toSet());
    }

    private Category getCategory(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

}
