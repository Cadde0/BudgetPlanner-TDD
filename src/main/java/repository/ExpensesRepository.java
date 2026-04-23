package repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Objects;

/**
 * Repository for operations on the expenses table.
 * Handles category management and other expense-related database interactions.
 */
@Repository
public class ExpensesRepository {
    private static final String EXPENSE_EXISTS_SQL = "SELECT COUNT(*) FROM expenses WHERE id = ?";
    private static final String CATEGORY_EXISTS_SQL = "SELECT COUNT(*) FROM category WHERE id = ?";
    private static final String UPDATE_CATEGORY_SQL = "UPDATE expenses SET category_id = ? WHERE id = ?";
    private static final String INSERT_EXPENSE_SQL = "INSERT INTO expenses (amount, description) VALUES (?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public ExpensesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Inserts a new expense record into the database.
     * Description is optional and can be empty.
     *
     * @param expense a map containing expense data (must include 'amount' > 0)
     * @return generated id of the new expense row
     */
    public int addExpense(Map<String, Object> expense) {
        Number amount = extractAndValidateAmount(expense);
        String description = extractDescription(expense);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_EXPENSE_SQL, new String[] { "id" });
            ps.setObject(1, amount);
            ps.setString(2, description);
            return ps;
        }, keyHolder);

        return requireGeneratedId(keyHolder);
    }

    private Number extractAndValidateAmount(Map<String, Object> expense) {
        Objects.requireNonNull(expense, "Expense map must not be null");
        if (!expense.containsKey("amount")) {
            throw new IllegalArgumentException("Expense must contain 'amount'");
        }

        Number amount = (Number) expense.get("amount");
        if (amount == null || amount.doubleValue() <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }
        return amount;
    }

    private String extractDescription(Map<String, Object> expense) {
        return Objects.toString(expense.get("description"), "");
    }

    private int requireGeneratedId(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id for expense");
        }
        return key.intValue();
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
