package repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import presentation.BudgetPlannerApplication;

/**
 * Integration tests for ExpensesRepository layer operations.
 * Tests use the real database to verify category management.
 */
@SpringBootTest(classes = BudgetPlannerApplication.class)
class ExpensesRepositoryTest {

    @Autowired
    private ExpensesRepository expensesRepository;

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

    @Test
    void testAddExpense() {
        Map<String, Object> expense = expenseWithAmountAndDescription(300, "Groceries");

        int id = expensesRepository.addExpense(expense);

        assertTrue(id > 0);
    }

    @Test
    void testAddExpenseWithEmptyDescription() {
        Map<String, Object> expense = expenseWithAmountAndDescription(120, "");

        int id = expensesRepository.addExpense(expense);

        assertTrue(id > 0);
    }

    @Test
    void testAddExpenseWithoutDescription() {
        Map<String, Object> expense = expenseWithAmount(90);

        int id = expensesRepository.addExpense(expense);

        assertTrue(id > 0);
    }

    @Test
    void testAddExpenseWithNullMapThrows() {
        assertThrows(NullPointerException.class, () -> expensesRepository.addExpense(null));
    }

    @Test
    void testAddExpenseWithMissingAmountThrows() {
        Map<String, Object> expense = new HashMap<>();

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.addExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testAddExpenseWithZeroAmountThrows() {
        Map<String, Object> expense = expenseWithAmount(0);

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.addExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testAddExpenseWithNegativeAmountThrows() {
        Map<String, Object> expense = expenseWithAmount(-10);

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.addExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testUpdateExpense() {
        int id = expensesRepository.addExpense(expenseWithAmountAndDescription(200, "Lunch"));
        Map<String, Object> expense = expenseWithAmountAndDescription(260, "Dinner");
        expense.put("id", id);

        boolean updated = expensesRepository.updateExpense(expense);

        assertTrue(updated);
    }

    @Test
    void testUpdateExpenseWithEmptyDescription() {
        int id = expensesRepository.addExpense(expenseWithAmountAndDescription(120, "Taxi"));
        Map<String, Object> expense = expenseWithAmountAndDescription(140, "");
        expense.put("id", id);

        boolean updated = expensesRepository.updateExpense(expense);

        assertTrue(updated);
    }

    @Test
    void testUpdateExpenseWithNullMapThrows() {
        assertThrows(NullPointerException.class, () -> expensesRepository.updateExpense(null));
    }

    @Test
    void testUpdateExpenseWithMissingIdThrows() {
        Map<String, Object> expense = expenseWithAmountAndDescription(140, "Coffee");

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.updateExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testUpdateExpenseWithMissingAmountThrows() {
        int id = expensesRepository.addExpense(expenseWithAmountAndDescription(110, "Snacks"));
        Map<String, Object> expense = new HashMap<>();
        expense.put("id", id);

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.updateExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testUpdateExpenseWithZeroAmountThrows() {
        int id = expensesRepository.addExpense(expenseWithAmountAndDescription(150, "Bills"));
        Map<String, Object> expense = expenseWithAmountAndDescription(0, "Bills");
        expense.put("id", id);

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.updateExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testUpdateExpenseWithNonExistentIdReturnsFalse() {
        Map<String, Object> expense = expenseWithAmountAndDescription(180, "Utilities");
        expense.put("id", 999999);

        boolean updated = expensesRepository.updateExpense(expense);

        assertFalse(updated);
    }

    @Test
    void testSetCategoryForExpense() {
        // Act: Set category for an expense
        expensesRepository.setCategoryForExpense(6, 1);

        // Assert: Verify no exception was thrown (method executed successfully on real
        // database)
        assertTrue(true, "Category was successfully set for the expense");
    }

    @Test
    void testSetCategoryForExpenseThrowsOnNullCategory() {
        // Act & Assert: Should throw exception for invalid category ID
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.setCategoryForExpense(1, 0),
                "Should throw exception for invalid category ID");
    }

    @Test
    void testSetCategoryForExpenseThrowsOnEmptyCategory() {
        // Act & Assert: Should throw exception for negative category ID
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.setCategoryForExpense(1, -1),
                "Should throw exception for negative category ID");
    }

    @Test
    void testSetCategoryThrowsWhenExpenseDoesNotExist() {
        // Act & Assert: Should throw exception when expense ID does not exist
        assertThrows(RuntimeException.class,
                () -> expensesRepository.setCategoryForExpense(99999, 1),
                "Should throw exception when expense ID does not exist");
    }

    @Test
    void testSetCategoryThrowsWhenCategoryDoesNotExist() {
        // Act & Assert: Should throw exception when category ID does not exist
        assertThrows(RuntimeException.class,
                () -> expensesRepository.setCategoryForExpense(1, 99999),
                "Should throw exception when category ID does not exist");
    }
}
