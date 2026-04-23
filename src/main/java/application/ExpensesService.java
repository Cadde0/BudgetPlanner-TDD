package application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.ExpensesRepository;

import java.util.Map;

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
     * Adds a new expense record.
     *
     * @param expense expense payload map
     * @return generated id of the inserted expense
     */
    @Transactional
    public int addExpense(Map<String, Object> expense) {
        validateExpenseForCreate(expense);
        return expensesRepository.addExpense(expense);
    }

    /**
     * Updates an existing expense.
     *
     * @param expense map containing expense update payload
     * @return true when update succeeded
     */
    @Transactional
    public boolean updateExpense(Map<String, Object> expense) {
        return expensesRepository.updateExpense(expense);
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

    private void validateExpenseForCreate(Map<String, Object> expense) {
        if (expense == null) {
            throw new NullPointerException("Expense map cannot be null");
        }

        Object amountValue = expense.get("amount");
        if (!(amountValue instanceof Number amountNumber) || amountNumber.doubleValue() <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }

        Object categoryValue = expense.get("categoryId");
        if (!(categoryValue instanceof Number categoryNumber) || categoryNumber.intValue() <= 0) {
            throw new IllegalArgumentException("Expense must contain 'categoryId'");
        }
    }

}
