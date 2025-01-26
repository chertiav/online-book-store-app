package com.achdev.onlinebookstoreapp.service.impl;

import static com.achdev.onlinebookstoreapp.utils.TestConstants.ERROR_MESSAGE_CATEGORY_NOT_FOUND;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INITIAL_INDEX;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.INVALID_ID;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.NEW_CATEGORY_NAME;
import static com.achdev.onlinebookstoreapp.utils.TestConstants.SAMPLE_TEST_ID;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createCategoryRequestDtoFromCategory;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createPage;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.createSampleCategoryRequestDto;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.loadAllCategories;
import static com.achdev.onlinebookstoreapp.utils.TestHelper.mapCategoryToDto;
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

import com.achdev.onlinebookstoreapp.dto.category.CategoryDto;
import com.achdev.onlinebookstoreapp.dto.category.CreateCategoryRequestDto;
import com.achdev.onlinebookstoreapp.exception.EntityNotFoundException;
import com.achdev.onlinebookstoreapp.mapper.CategoryMapper;
import com.achdev.onlinebookstoreapp.model.Category;
import com.achdev.onlinebookstoreapp.repository.category.CategoryRepository;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Implementation Test")
class CategoryServiceImplTest {
    private static List<Category> categories;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @BeforeAll
    static void setUp() {
        categories = loadAllCategories();
    }

    @Test
    @DisplayName("Find all categories")
    void findAll_ValidPageable_ShouldReturnPageOfCategoryDto() {
        Category category = categories.get(INITIAL_INDEX);
        CategoryDto categoryDto = mapCategoryToDto(category);
        Pageable pageable = PageRequest.of(0, 20);

        Page<Category> categoryPage = createPage(List.of(category), pageable);
        Page<CategoryDto> expected = createPage(List.of(categoryDto), pageable);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        //Then
        verifyPageContent(expected, actual);

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find all categories when repository returns empty page")
    void findAll_WhenRepositoryReturnsEmptyPage_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Category> categoryPage = createPage(List.of(), pageable);
        Page<CategoryDto> expected = createPage(List.of(), pageable);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        //When
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        //Then
        verifyPageContent(expected, actual);

        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("Find by Id should return CategoryDto when a valid ID is provided")
    void findById_ValidId_ShouldReturnCategoryDto() {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CategoryDto expected = mapCategoryToDto(category);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //When
        CategoryDto actual = categoryService.findById(category.getId());

        //Then
        validateObjectDto(expected, actual);

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find by Id should throw exception when the ID is invalid")
    void findById_InvalidId_ShouldReturnException() {
        //Given
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(INVALID_ID));

        //Then
        String expected = ERROR_MESSAGE_CATEGORY_NOT_FOUND + INVALID_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(categoryRepository, times(1)).findById(INVALID_ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Save category successfully when valid data is provided")
    void save_ValidData_ShouldReturnSavedCategoryDto() {
        //Given
        CreateCategoryRequestDto requestDto = createSampleCategoryRequestDto();
        Category categoryModel = categoryFromRequestDto(requestDto);

        Category category = categoryFromRequestDto(requestDto);
        category.setId(SAMPLE_TEST_ID);

        CategoryDto expected = mapCategoryToDto(category);
        when(categoryMapper.toModel(requestDto)).thenReturn(categoryModel);
        when(categoryRepository.save(categoryModel)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //When
        CategoryDto actual = categoryService.save(requestDto);

        //Then
        validateObjectDto(expected, actual);

        verify(categoryMapper, times(1)).toModel(requestDto);

        verify(categoryRepository, times(1)).save(categoryModel);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update category successfully when valid data is provided")
    void updateById_ValidData_ShouldReturnUpdatedCategoryDto() {
        //Given
        Category category = categories.get(INITIAL_INDEX);
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoFromCategory(
                NEW_CATEGORY_NAME,
                category
        );

        CategoryDto expected = mapCategoryToDto(category);
        expected.setName(NEW_CATEGORY_NAME);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        doAnswer(invocation -> {
            Category updatedCategory = invocation.getArgument(1);
            updatedCategory.setName(NEW_CATEGORY_NAME);
            return null;
        }).when(categoryMapper).updateCategoryFromDto(requestDto, category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //When
        CategoryDto actual = categoryService.updateById(category.getId(), requestDto);

        //Then
        validateObjectDto(expected, actual);

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryMapper, times(1)).updateCategoryFromDto(requestDto, category);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Should delete category successfully when valid ID is provided")
    void deleteById() {
        // Given
        doNothing().when(categoryRepository).deleteById(SAMPLE_TEST_ID);

        // When
        assertDoesNotThrow(() -> categoryService.deleteById(SAMPLE_TEST_ID));

        // Then
        verify(categoryRepository, times(1)).deleteById(SAMPLE_TEST_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    private Category categoryFromRequestDto(CreateCategoryRequestDto requestDto) {
        Category category = new Category();
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        return category;
    }
}
