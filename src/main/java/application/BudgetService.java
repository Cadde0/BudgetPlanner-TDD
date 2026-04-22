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
}
