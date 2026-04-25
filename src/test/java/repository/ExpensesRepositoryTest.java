package repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import presentation.BudgetPlannerApplication;

/**
 * Integration tests for ExpensesRepository layer operations.
 * Tests use the real database to verify category management.
 */
@SpringBootTest(classes = BudgetPlannerApplication.class)
@Transactional
class ExpensesRepositoryTest {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final List<Integer> createdExpenseIds = new ArrayList<>();
    private final List<Integer> createdCategoryIds = new ArrayList<>();

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

    private Map<String, Object> expenseWithAmountDescriptionAndCategory(Number amount, String description, int categoryId) {
        Map<String, Object> expense = expenseWithAmountAndDescription(amount, description);
        expense.put("categoryId", categoryId);
        return expense;
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        for (Integer id : createdExpenseIds) {
            jdbcTemplate.update("DELETE FROM expenses WHERE id = ?", id);
        }
        createdExpenseIds.clear();

        for (Integer id : createdCategoryIds) {
            jdbcTemplate.update("DELETE FROM category WHERE id = ?", id);
        }
        createdCategoryIds.clear();
    }

    private int createTempCategory() {
        String name = "TDD Expense Category " + System.nanoTime();
        jdbcTemplate.update("INSERT INTO category (name) VALUES (?)", name);
        Integer id = jdbcTemplate.queryForObject(
                "SELECT id FROM category WHERE name = ? ORDER BY id DESC LIMIT 1",
                Integer.class,
                name);
        if (id == null) {
            throw new IllegalStateException("Failed to create temporary category");
        }
        createdCategoryIds.add(id);
        return id;
    }

    @Test
    void testAddExpense() {
        int categoryId = createTempCategory();
        Map<String, Object> expense = expenseWithAmountDescriptionAndCategory(300, "Groceries", categoryId);

        int id = expensesRepository.addExpense(expense);
        createdExpenseIds.add(id);

        assertTrue(id > 0);
    }

    @Test
    void testAddExpenseWithEmptyDescription() {
        int categoryId = createTempCategory();
        Map<String, Object> expense = expenseWithAmountDescriptionAndCategory(120, "", categoryId);

        int id = expensesRepository.addExpense(expense);
        createdExpenseIds.add(id);

        assertTrue(id > 0);
    }

    @Test
    void testAddExpenseWithoutDescription() {
        int categoryId = createTempCategory();
        Map<String, Object> expense = expenseWithAmount(90);
        expense.put("categoryId", categoryId);

        int id = expensesRepository.addExpense(expense);
        createdExpenseIds.add(id);

        assertTrue(id > 0);
    }

    @Test
    void testAddExpenseWithCategoryPersistsCategoryId() {
        int categoryId = createTempCategory();
        Map<String, Object> expense = expenseWithAmountDescriptionAndCategory(410, "Fuel", categoryId);

        int expenseId = expensesRepository.addExpense(expense);
        createdExpenseIds.add(expenseId);

        Integer persistedCategoryId = jdbcTemplate.queryForObject(
                "SELECT category_id FROM expenses WHERE id = ?",
                Integer.class,
                expenseId);

        assertEquals(categoryId, persistedCategoryId);
    }

    @Test
    void testAddExpenseWithMissingCategoryThrows() {
        Map<String, Object> expense = expenseWithAmountAndDescription(210, "Bus pass");

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.addExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
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
        int categoryId = createTempCategory();
        int id = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(200, "Lunch", categoryId));
        createdExpenseIds.add(id);
        Map<String, Object> expense = expenseWithAmountAndDescription(260, "Dinner");
        expense.put("id", id);

        boolean updated = expensesRepository.updateExpense(expense);

        assertTrue(updated);
    }

    @Test
    void testUpdateExpenseWithEmptyDescription() {
        int categoryId = createTempCategory();
        int id = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(120, "Taxi", categoryId));
        createdExpenseIds.add(id);
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
        int categoryId = createTempCategory();
        int id = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(110, "Snacks", categoryId));
        createdExpenseIds.add(id);
        Map<String, Object> expense = new HashMap<>();
        expense.put("id", id);

        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> expensesRepository.updateExpense(expense));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testUpdateExpenseWithZeroAmountThrows() {
        int categoryId = createTempCategory();
        int id = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(150, "Bills", categoryId));
        createdExpenseIds.add(id);
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
    void testDeleteExpense() {
        int categoryId = createTempCategory();
        int id = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(100, "Breakfast", categoryId));
        
        boolean deleted = expensesRepository.deleteExpense(id);

        assertTrue(deleted);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM expenses WHERE id = ?", Integer.class, id);
        assertEquals(0, count);
    }

    @Test
    void testDeleteExpenseWithNonExistentIdReturnsFalse() {
        boolean deleted = expensesRepository.deleteExpense(999999);
        assertFalse(deleted);
    }

    @Test
    void testDeleteExpenseWithInvalidIdThrows() {
        Exception ex1 = assertThrows(InvalidDataAccessApiUsageException.class, () -> expensesRepository.deleteExpense(0));
        assertTrue(ex1.getCause() instanceof IllegalArgumentException);

        Exception ex2 = assertThrows(InvalidDataAccessApiUsageException.class, () -> expensesRepository.deleteExpense(-1));
        assertTrue(ex2.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testSetCategoryForExpense() {
        int categoryId = createTempCategory();
        int expenseId = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(100, "Breakfast", categoryId));
        createdExpenseIds.add(expenseId);
        int newCategoryId = createTempCategory();

        // Act: Set category for an expense
        expensesRepository.setCategoryForExpense(expenseId, newCategoryId);

        // Assert: Verify category was updated
        Integer persistedCategoryId = jdbcTemplate.queryForObject(
                "SELECT category_id FROM expenses WHERE id = ?",
                Integer.class,
                expenseId);
        assertEquals(newCategoryId, persistedCategoryId);
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

    @Test
    void testGetAllExpensesWithCategory() {
        int categoryId = createTempCategory();
        String categoryName = jdbcTemplate.queryForObject("SELECT name FROM category WHERE id = ?", String.class, categoryId);
        
        int id1 = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(100, "Expense 1", categoryId));
        int id2 = expensesRepository.addExpense(expenseWithAmountDescriptionAndCategory(200, "Expense 2", categoryId));
        createdExpenseIds.add(id1);
        createdExpenseIds.add(id2);

        List<Map<String, Object>> results = expensesRepository.getAllExpensesWithCategory();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        Map<String, Object> exp1 = results.stream().filter(m -> (int)m.get("id") == id1).findFirst().orElseThrow();
        assertEquals(100.0, ((Number)exp1.get("amount")).doubleValue());
        assertEquals("Expense 1", exp1.get("description"));
        assertEquals(categoryName, exp1.get("categoryName"));
        assertEquals(categoryId, exp1.get("categoryId"));

        Map<String, Object> exp2 = results.stream().filter(m -> (int)m.get("id") == id2).findFirst().orElseThrow();
        assertEquals(200.0, ((Number)exp2.get("amount")).doubleValue());
        assertEquals("Expense 2", exp2.get("description"));
        assertEquals(categoryName, exp2.get("categoryName"));
        assertEquals(categoryId, exp2.get("categoryId"));
    }
}
