package repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for operations on the expenses table.
 * Handles category management and other expense-related database interactions.
 */
@Repository
public class ExpensesRepository {
    private static final String EXPENSE_EXISTS_SQL = "SELECT COUNT(*) FROM expenses WHERE id = ?";
    private static final String CATEGORY_EXISTS_SQL = "SELECT COUNT(*) FROM category WHERE id = ?";
    private static final String UPDATE_CATEGORY_SQL = "UPDATE expenses SET category_id = ? WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ExpensesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Sets the category for a specific expense record.
     *
     * @param expenseId  the ID of the expense to update
     * @param categoryId the category ID to set (must be positive)
     * @throws IllegalArgumentException if categoryId is not positive
     * @throws RuntimeException         if the update fails
     */
    public void setCategoryForExpense(int expenseId, int categoryId) {
        validatePositiveId(expenseId, "Expense ID");
        validatePositiveId(categoryId, "Category ID");

        assertExists(expenseExists(expenseId), "Expense ID", expenseId);
        assertExists(categoryExists(categoryId), "Category ID", categoryId);

        jdbcTemplate.update(UPDATE_CATEGORY_SQL, categoryId, expenseId);
    }

    private void validatePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    private void assertExists(boolean exists, String fieldName, int id) {
        if (!exists) {
            throw new RuntimeException(fieldName + " does not exist: " + id);
        }
    }

    private boolean expenseExists(int expenseId) {
        Integer count = jdbcTemplate.queryForObject(EXPENSE_EXISTS_SQL, Integer.class, expenseId);
        return count != null && count > 0;
    }

    private boolean categoryExists(int categoryId) {
        Integer count = jdbcTemplate.queryForObject(CATEGORY_EXISTS_SQL, Integer.class, categoryId);
        return count != null && count > 0;
    }
}
