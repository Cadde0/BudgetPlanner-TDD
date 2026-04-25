package application;

import org.springframework.stereotype.Service;
import repository.BudgetRepository;
import java.util.List;
import java.util.Map;

/**
 * Service for generic budget-related operations.
 * Delegates to BudgetRepository for database access.
 */
@Service
public class BudgetService {
    private static final String EXPENSES_TABLE = "expenses";
    private static final String INCOME_TABLE = "income";
    private static final String AMOUNT_FIELD = "amount";

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    /**
     * Retrieves all rows from the specified table using the repository.
     *
     * @param tableName the name of the table to query
     * @return a list of rows (each row is a Map<String, Object>)
     */
    public List<Map<String, Object>> getAllFromTable(String tableName) {
        return budgetRepository.queryAllRows(tableName);
    }

    /**
     * Retrieves a single row by ID from the specified table using the repository.
     *
     * @param tableName the name of the table to query
     * @param id the ID value to search for
     * @return a row as Map<String, Object>, or null if not found
     */
    public Map<String, Object> getById(String tableName, int id) {
        return budgetRepository.queryById(tableName, id);
    }

    /**
     * Calculates the total sum of all expenses.
     *
     * @return sum of all expense amounts
     */
    public double calculateTotalExpenses() {
        return calculateTotalForTable(EXPENSES_TABLE);
    }

    /**
     * Calculates the total sum of all income.
     *
     * @return sum of all income amounts
     */
    public double calculateTotalIncome() {
        return calculateTotalForTable(INCOME_TABLE);
    }

    /**
     * Calculates the remaining budget (Total Income - Total Expenses).
     *
     * @return remaining budget amount
     */
    public double calculateRemainingBudget() {
        return calculateTotalIncome() - calculateTotalExpenses();
    }

    private double calculateTotalForTable(String tableName) {
        List<Map<String, Object>> rows = budgetRepository.queryAllRows(tableName);
        return sumAmount(rows);
    }

    private double sumAmount(List<Map<String, Object>> rows) {
        if (rows == null) return 0.0;
        return rows.stream()
                .map(row -> row.get(AMOUNT_FIELD))
                .filter(Number.class::isInstance)
                .mapToDouble(amount -> ((Number) amount).doubleValue())
                .sum();
    }
}
