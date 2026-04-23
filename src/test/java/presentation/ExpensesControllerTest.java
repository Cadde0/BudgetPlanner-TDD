package presentation;

import application.BudgetService;
import application.ExpensesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ExpensesController.
 * Verifies that category assignment requests are handled in the controller
 * layer.
 */
class ExpensesControllerTest {

    @Mock
    private ExpensesService expensesService;

    @Mock
    private BudgetService budgetService;

    private ExpensesController expensesController;

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
        expensesController = new ExpensesController(expensesService, budgetService);
    }

    @Test
    void testGetAllExpenses() {
        List<Map<String, Object>> expected = Collections.singletonList(Collections.singletonMap("id", 1));
        when(budgetService.getAllFromTable("expenses")).thenReturn(expected);

        List<Map<String, Object>> result = expensesController.getAllExpenses();

        assertEquals(expected, result);
        verify(budgetService).getAllFromTable("expenses");
    }

    @Test
    void testGetAllCategories() {
        List<Map<String, Object>> expected = Collections.singletonList(Collections.singletonMap("id", 1));
        when(budgetService.getAllFromTable("category")).thenReturn(expected);

        List<Map<String, Object>> result = expensesController.getAllCategories();

        assertEquals(expected, result);
        verify(budgetService).getAllFromTable("category");
    }

    @Test
    void testAddExpense() {
        Map<String, Object> expense = expenseWithAmountAndDescription(300, "Internet");
        when(expensesService.addExpense(expense)).thenReturn(10);

        ResponseEntity<Integer> response = expensesController.addExpense(expense);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10, response.getBody());
        verify(expensesService).addExpense(expense);
    }

    @Test
    void testAddExpenseWithEmptyDescription() {
        Map<String, Object> expense = expenseWithAmountAndDescription(300, "");
        when(expensesService.addExpense(expense)).thenReturn(11);

        ResponseEntity<Integer> response = expensesController.addExpense(expense);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(11, response.getBody());
        verify(expensesService).addExpense(expense);
    }

    @Test
    void testAddExpenseWithoutDescription() {
        Map<String, Object> expense = expenseWithAmount(300);
        when(expensesService.addExpense(expense)).thenReturn(12);

        ResponseEntity<Integer> response = expensesController.addExpense(expense);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(12, response.getBody());
        verify(expensesService).addExpense(expense);
    }

    @Test
    void testAddExpenseThrowsWhenAmountMissing() {
        Map<String, Object> expense = new HashMap<>();
        doThrow(new IllegalArgumentException("Expense must contain 'amount'"))
                .when(expensesService).addExpense(expense);

        assertThrows(IllegalArgumentException.class,
                () -> expensesController.addExpense(expense));
    }

    @Test
    void testSetCategoryForExpense() {
        ResponseEntity<Boolean> response = expensesController.setCategory(6, 1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody());
        verify(expensesService).setCategory(6, 1);
    }

    @Test
    void testSetCategoryThrowsOnInvalidExpenseId() {
        doThrow(new IllegalArgumentException("Expense ID must be positive"))
                .when(expensesService).setCategory(0, 1);

        assertThrows(IllegalArgumentException.class,
                () -> expensesController.setCategory(0, 1));
    }

    @Test
    void testSetCategoryThrowsOnInvalidCategoryId() {
        doThrow(new IllegalArgumentException("Category ID must be positive"))
                .when(expensesService).setCategory(1, 0);

        assertThrows(IllegalArgumentException.class,
                () -> expensesController.setCategory(1, 0));
    }

    @Test
    void testSetCategoryThrowsWhenExpenseDoesNotExist() {
        doThrow(new RuntimeException("Expense ID does not exist: 99999"))
                .when(expensesService).setCategory(99999, 1);

        assertThrows(RuntimeException.class,
                () -> expensesController.setCategory(99999, 1));
    }

    @Test
    void testSetCategoryThrowsWhenCategoryDoesNotExist() {
        doThrow(new RuntimeException("Category ID does not exist: 99999"))
                .when(expensesService).setCategory(1, 99999);

        assertThrows(RuntimeException.class,
                () -> expensesController.setCategory(1, 99999));
    }
}