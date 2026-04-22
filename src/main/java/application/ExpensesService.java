package application;

import org.springframework.stereotype.Service;
import repository.ExpensesRepository;

/**
 * Service for expense-related operations.
 * Delegates to ExpensesRepository for database access.
 */
@Service
public class ExpensesService {

    private final ExpensesRepository expensesRepository;

    public ExpensesService(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }

    /**
     * Sets the category for a specific expense.
     *
     * @param expenseId  the expense ID (must be positive)
     * @param categoryId the category ID (must be positive)
     * @throws IllegalArgumentException if any ID is not positive
     */
    public void setCategory(int expenseId, int categoryId) {
        validatePositiveId(expenseId, "Expense ID");
        validatePositiveId(categoryId, "Category ID");

        expensesRepository.setCategoryForExpense(expenseId, categoryId);
    }

    private void validatePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

}
