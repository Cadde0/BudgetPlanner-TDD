package application;

import org.springframework.stereotype.Service;
import repository.BudgetRepository;
import java.util.List;
import java.util.Map;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    /**
     * Retrieves all rows from the specified table using the repository.
     */
    public List<Map<String, Object>> getAllFromTable(String tableName) {
        return budgetRepository.queryAllRows(tableName);
    }

    /**
     * Retrieves a single row by ID from the specified table using the repository.
     */
    public Map<String, Object> getById(String tableName, int id) {
        return budgetRepository.queryById(tableName, id);
    }
}
