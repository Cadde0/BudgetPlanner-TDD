
package application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.ExpensesRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Object> expenseWithAmount(Number amount) {
        Map<String, Object> expense = new HashMap<>();
        expense.put("amount", amount);
        return expense;
    }

    private Map<String, Object> expenseWithAmountAndDescription(Number amount, String description) {
        Map<String, Object> expense = expenseWithAmount(amount);
        expense.put("description", description);
        return expense;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddExpense() {
        Map<String, Object> expense = expenseWithAmountAndDescription(250, "Transport");
        expense.put("categoryId", 4);

        when(expensesRepository.addExpense(anyMap())).thenReturn(1);

        int id = expensesService.addExpense(expense);

        assertEquals(1, id);
        verify(expensesRepository).addExpense(expense);
    }

    @Test
    void testAddExpenseWithEmptyDescription() {
        Map<String, Object> expense = expenseWithAmountAndDescription(250, "");
        expense.put("categoryId", 5);

        when(expensesRepository.addExpense(anyMap())).thenReturn(2);

        int id = expensesService.addExpense(expense);

        assertEquals(2, id);
        verify(expensesRepository).addExpense(expense);
    }

    @Test
    void testAddExpenseThrowsWhenAmountMissing() {
        Map<String, Object> expense = new HashMap<>();
        expense.put("categoryId", 2);

        assertThrows(IllegalArgumentException.class, () -> expensesService.addExpense(expense));
        verifyNoInteractions(expensesRepository);
    }

    @Test
    void testAddExpenseThrowsWhenAmountInvalid() {
        Map<String, Object> expense = expenseWithAmount(0);
        expense.put("categoryId", 2);

        assertThrows(IllegalArgumentException.class, () -> expensesService.addExpense(expense));
        verifyNoInteractions(expensesRepository);
    }

    @Test
    void testAddExpenseThrowsWhenCategoryMissing() {
        Map<String, Object> expense = expenseWithAmountAndDescription(120, "Parking");

        assertThrows(IllegalArgumentException.class, () -> expensesService.addExpense(expense));
        verifyNoInteractions(expensesRepository);
    }

    @Test
    void testAddExpenseThrowsWhenCategoryInvalid() {
        Map<String, Object> expense = expenseWithAmountAndDescription(120, "Parking");
        expense.put("categoryId", 0);

        assertThrows(IllegalArgumentException.class, () -> expensesService.addExpense(expense));
        verifyNoInteractions(expensesRepository);
    }

    @Test
    void testUpdateExpense() {
        Map<String, Object> expense = expenseWithAmountAndDescription(300, "Updated");
        expense.put("id", 7);
        when(expensesRepository.updateExpense(anyMap())).thenReturn(true);

        boolean updated = expensesService.updateExpense(expense);

        assertTrue(updated);
        verify(expensesRepository).updateExpense(expense);
    }

    @Test
    void testUpdateExpenseThrowsWhenAmountInvalid() {
        Map<String, Object> expense = expenseWithAmountAndDescription(0, "Invalid");
        expense.put("id", 7);
        doThrow(new IllegalArgumentException("Expense amount must be positive"))
                .when(expensesRepository).updateExpense(expense);

        assertThrows(IllegalArgumentException.class, () -> expensesService.updateExpense(expense));
    }

    @Test
    void testDeleteExpense() {
        when(expensesRepository.deleteExpense(10)).thenReturn(true);

        boolean deleted = expensesService.deleteExpense(10);

        assertTrue(deleted);
        verify(expensesRepository).deleteExpense(10);
    }

    @Test
    void testDeleteExpenseReturnsFalseWhenNotFound() {
        when(expensesRepository.deleteExpense(999)).thenReturn(false);

        boolean deleted = expensesService.deleteExpense(999);

        assertFalse(deleted);
        verify(expensesRepository).deleteExpense(999);
    }

    @Test
    void testDeleteExpenseThrowsOnInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> expensesService.deleteExpense(0));
        assertThrows(IllegalArgumentException.class, () -> expensesService.deleteExpense(-1));
        verifyNoInteractions(expensesRepository);
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

    @Test
    void testGetExpensesGroupedByCategory() {
        List<Map<String, Object>> mockResults = new ArrayList<>();
        
        Map<String, Object> exp1 = new HashMap<>();
        exp1.put("id", 1);
        exp1.put("amount", 100.0);
        exp1.put("description", "Lunch");
        exp1.put("categoryId", 1);
        exp1.put("categoryName", "Food");
        
        Map<String, Object> exp2 = new HashMap<>();
        exp2.put("id", 2);
        exp2.put("amount", 200.0);
        exp2.put("description", "Dinner");
        exp2.put("categoryId", 1);
        exp2.put("categoryName", "Food");
        
        Map<String, Object> exp3 = new HashMap<>();
        exp3.put("id", 3);
        exp3.put("amount", 50.0);
        exp3.put("description", "Bus");
        exp3.put("categoryId", 2);
        exp3.put("categoryName", "Transport");
        
        mockResults.add(exp1);
        mockResults.add(exp2);
        mockResults.add(exp3);
        
        when(expensesRepository.getAllExpensesWithCategory()).thenReturn(mockResults);
        
        List<Map<String, Object>> grouped = expensesService.getExpensesGroupedByCategory();
        
        assertNotNull(grouped);
        assertEquals(2, grouped.size());
        
        Map<String, Object> foodCategory = grouped.stream()
                .filter(m -> "Food".equals(m.get("categoryName")))
                .findFirst().orElseThrow();
        assertEquals(300.0, (Double)foodCategory.get("total"));
        assertEquals(2, ((List<?>)foodCategory.get("expenses")).size());
        
        Map<String, Object> transportCategory = grouped.stream()
                .filter(m -> "Transport".equals(m.get("categoryName")))
                .findFirst().orElseThrow();
        assertEquals(50.0, (Double)transportCategory.get("total"));
        assertEquals(1, ((List<?>)transportCategory.get("expenses")).size());
    }
}
