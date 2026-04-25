package application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.ExpensesRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * Retrieves all expenses grouped by category, including the total per category.
     *
     * @return a list of maps, each containing category name, total sum, and a list
     *         of expenses
     */
    public List<Map<String, Object>> getExpensesGroupedByCategory() {
        List<Map<String, Object>> allExpenses = expensesRepository.getAllExpensesWithCategory();

        Map<String, List<Map<String, Object>>> grouped = allExpenses.stream()
                .collect(Collectors.groupingBy(e -> (String) e.get("categoryName")));

        List<Map<String, Object>> results = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
            String categoryName = entry.getKey();
            List<Map<String, Object>> expenses = entry.getValue();

            double total = expenses.stream()
                    .mapToDouble(e -> ((Number) e.get("amount")).doubleValue())
                    .sum();

            // Retrieve the category limit from the first expense in the group
            Map<String, Object> firstExpense = expenses.get(0);
            Double limit = null;
            if (firstExpense.containsKey("categoryLimit") && firstExpense.get("categoryLimit") != null) {
                limit = ((Number) firstExpense.get("categoryLimit")).doubleValue();
            }

            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("categoryName", categoryName);
            categoryData.put("total", total);
            categoryData.put("limit", limit);
            categoryData.put("expenses", expenses);
            results.add(categoryData);
        }

        return results;
    }

    /**
     * Adds a new expense record.
     *
     * @param expense expense payload map
...

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
     * Deletes an existing expense.
     *
     * @param id the ID of the expense to delete
     * @return true when deletion succeeded
     */
    @Transactional
    public boolean deleteExpense(int id) {
        validatePositiveId(id, "Expense ID");
        return expensesRepository.deleteExpense(id);
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
