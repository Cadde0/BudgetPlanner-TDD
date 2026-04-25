package presentation;

import application.BudgetService;
import application.ExpensesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpensesController {

    private final ExpensesService expensesService;
    private final BudgetService budgetService;

    public ExpensesController(ExpensesService expensesService, BudgetService budgetService) {
        this.expensesService = expensesService;
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<Map<String, Object>> getAllExpenses() {
        return budgetService.getAllFromTable("expenses");
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> getAllCategories() {
        return budgetService.getAllFromTable("category");
    }

    @PostMapping
    public ResponseEntity<Integer> addExpense(@RequestBody Map<String, Object> expense) {
        int id = expensesService.addExpense(expense);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateExpense(@PathVariable int id, @RequestBody Map<String, Object> expense) {
        expense.put("id", id);
        boolean updated = expensesService.updateExpense(expense);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteExpense(@PathVariable int id) {
        boolean deleted = expensesService.deleteExpense(id);
        return ResponseEntity.ok(deleted);
    }

    @PutMapping("/{expenseId}/category/{categoryId}")
    public ResponseEntity<Boolean> setCategory(@PathVariable int expenseId, @PathVariable int categoryId) {
        expensesService.setCategory(expenseId, categoryId);
        return ResponseEntity.ok(true);
    }
}
