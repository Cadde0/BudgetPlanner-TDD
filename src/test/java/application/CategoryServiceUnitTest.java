package application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.CategoryRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService using Mockito to mock the repository layer.
 * These tests verify the service logic without requiring a real database.
 */
class CategoryServiceUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Add category tests
    @Test
    void testAddCategoryWithValidMapSucceeds() {
        Map<String, Object> category = new HashMap<>();
        category.put("name", "Groceries");
        when(categoryRepository.addCategory(category)).thenReturn(7);

        int id = categoryService.addCategory(category);

        assertEquals(7, id);
        verify(categoryRepository).addCategory(category);
    }

    @Test
    void testAddCategoryWithValidMapAndLimitSucceeds() {
        Map<String, Object> category = new HashMap<>();
        category.put("name", "Groceries");
        category.put("category_limit", 1200);
        when(categoryRepository.addCategory(category)).thenReturn(8);

        int id = categoryService.addCategory(category);

        assertEquals(8, id);
        verify(categoryRepository).addCategory(category);
    }

    @Test
    void testAddCategoryThrowsWhenMapIsNull() {
        assertThrows(NullPointerException.class,
                () -> categoryService.addCategory(null),
                "Should throw NullPointerException when category map is null");

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testAddCategoryThrowsWhenNameIsMissing() {
        Map<String, Object> category = new HashMap<>();

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.addCategory(category),
                "Should throw IllegalArgumentException when name is missing");

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testAddCategoryThrowsWhenNameIsNull() {
        Map<String, Object> category = new HashMap<>();
        category.put("name", null);

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.addCategory(category),
                "Should throw IllegalArgumentException when name is null");

        verifyNoInteractions(categoryRepository);
    }

    // Update category tests
    @Test
    void testUpdateCategoryWithValidMapSucceeds() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "Groceries");
        when(categoryRepository.updateCategory(category)).thenReturn(true);

        boolean updated = categoryService.updateCategory(category);

        assertTrue(updated);
        verify(categoryRepository).updateCategory(category);
    }

    @Test
    void testUpdateCategoryWithValidMapAndLimitSucceeds() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "Groceries");
        category.put("category_limit", 900);
        when(categoryRepository.updateCategory(category)).thenReturn(true);

        boolean updated = categoryService.updateCategory(category);

        assertTrue(updated);
        verify(categoryRepository).updateCategory(category);
    }

    @Test
    void testUpdateCategoryThrowsWhenMapIsNull() {
        assertThrows(NullPointerException.class,
                () -> categoryService.updateCategory(null),
                "Should throw NullPointerException when category map is null");

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testUpdateCategoryThrowsWhenOldNameIsMissing() {
        Map<String, Object> category = new HashMap<>();
        category.put("name", "Groceries");

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(category),
                "Should throw IllegalArgumentException when id is missing");

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testUpdateCategoryThrowsWhenOldNameIsNull() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", null);
        category.put("name", "Groceries");

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(category),
                "Should throw IllegalArgumentException when id is null");

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testUpdateCategoryThrowsWhenNewNameIsMissing() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(category),
                "Should throw IllegalArgumentException when name is missing");

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testUpdateCategoryThrowsWhenNewNameIsNull() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", null);

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(category),
                "Should throw IllegalArgumentException when name is null");

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testUpdateCategoryReturnsFalseWhenCategoryNotFound() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 9999);
        category.put("name", "Updated");
        when(categoryRepository.updateCategory(category)).thenReturn(false);

        boolean updated = categoryService.updateCategory(category);

        assertFalse(updated);
        verify(categoryRepository).updateCategory(category);
    }

    // Delete category tests
    @Test
    void testDeleteCategoryWithValidIdSucceeds() {
        when(categoryRepository.deleteCategory(1)).thenReturn(true);

        boolean deleted = categoryService.deleteCategory(1);

        assertTrue(deleted);
        verify(categoryRepository).deleteCategory(1);
    }

    @Test
    void testDeleteCategoryReturnsFalseWhenCategoryNotFound() {
        when(categoryRepository.deleteCategory(9999)).thenReturn(false);

        boolean deleted = categoryService.deleteCategory(9999);

        assertFalse(deleted);
        verify(categoryRepository).deleteCategory(9999);
    }

    @Test
    void testDeleteCategoryReturnsFalseWhenIdIsNegative() {
        when(categoryRepository.deleteCategory(-1)).thenReturn(false);

        boolean deleted = categoryService.deleteCategory(-1);

        assertFalse(deleted);
        verify(categoryRepository).deleteCategory(-1);
    }

    @Test
    void testDeleteCategoryReturnsFalseWhenIdIsZero() {
        when(categoryRepository.deleteCategory(0)).thenReturn(false);

        boolean deleted = categoryService.deleteCategory(0);

        assertFalse(deleted);
        verify(categoryRepository).deleteCategory(0);
    }
}
