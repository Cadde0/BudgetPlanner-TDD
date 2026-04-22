package application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.ExpensesRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpensesService using Mockito to mock the repository layer.
 * These tests verify the service logic without requiring a real database.
 */
class ExpensesServiceUnitTest {

    @Mock
    private ExpensesRepository expensesRepository;

    @InjectMocks
    private ExpensesService expensesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies that setCategory calls the repository with correct parameters.
     */
    @Test
    void testSetCategoryForExpense() {
        // Act: Call service to set category
        expensesService.setCategory(1, 2);

        // Assert: Verify the repository was called with the correct parameters
        verify(expensesRepository).setCategoryForExpense(1, 2);
    }

    /**
     * Verifies that setCategory throws IllegalArgumentException when expense ID is
     * invalid.
     */
    @Test
    void testSetCategoryThrowsOnInvalidExpenseId() {
        // Act & Assert: Should throw IllegalArgumentException for invalid expense ID
        assertThrows(IllegalArgumentException.class,
                () -> expensesService.setCategory(0, 1),
                "Should throw IllegalArgumentException for invalid expense ID");

        verifyNoInteractions(expensesRepository);
    }

    /**
     * Verifies that setCategory throws IllegalArgumentException when category ID is
     * invalid.
     */
    @Test
    void testSetCategoryThrowsOnInvalidCategoryId() {
        // Act & Assert: Should throw IllegalArgumentException for invalid category ID
        assertThrows(IllegalArgumentException.class,
                () -> expensesService.setCategory(1, 0),
                "Should throw IllegalArgumentException for invalid category ID");

        verifyNoInteractions(expensesRepository);
    }

    /**
     * Verifies that setCategory propagates repository errors when expense ID does
     * not
     * exist.
     */
    @Test
    void testSetCategoryThrowsWhenExpenseDoesNotExist() {
        doThrow(new RuntimeException("Expense ID does not exist: 99999"))
                .when(expensesRepository).setCategoryForExpense(99999, 1);

        assertThrows(RuntimeException.class,
                () -> expensesService.setCategory(99999, 1),
                "Should throw when expense ID does not exist");
    }

    /**
     * Verifies that setCategory propagates repository errors when category ID does
     * not
     * exist.
     */
    @Test
    void testSetCategoryThrowsWhenCategoryDoesNotExist() {
        doThrow(new RuntimeException("Category ID does not exist: 99999"))
                .when(expensesRepository).setCategoryForExpense(1, 99999);

        assertThrows(RuntimeException.class,
                () -> expensesService.setCategory(1, 99999),
                "Should throw when category ID does not exist");
    }
}
