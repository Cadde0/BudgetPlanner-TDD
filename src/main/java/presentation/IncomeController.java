package presentation;

import application.IncomeService;
import application.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing income records.
 * Provides endpoints for CRUD operations on income.
 */
@RestController
@RequestMapping("/api/income")
public class IncomeController {
    private final IncomeService incomeService;
    private final BudgetService budgetService;

    public IncomeController(IncomeService incomeService, BudgetService budgetService) {
        this.incomeService = incomeService;
        this.budgetService = budgetService;
    }
    /**
     * Retrieves all income records.
     *
     * @return a list of all incomes as maps
     */
    @GetMapping
    public List<Map<String, Object>> getAllIncomes() {
        return budgetService.getAllFromTable("income");
    }

    /**
     * Adds a new income record.
     *
     * @param income the income data
     * @return the generated id of the new income
     */
    @PostMapping
    public ResponseEntity<Integer> addIncome(@RequestBody Map<String, Object> income) {
        int id = incomeService.addIncome(income);
        return ResponseEntity.ok(id);
    }

    /**
     * Updates an existing income record.
     *
     * @param id the id of the income to update
     * @param income the updated income data
     * @return true if the update was successful, false otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateIncome(@PathVariable int id, @RequestBody Map<String, Object> income) {
        income.put("id", id);
        boolean updated = incomeService.updateIncome(income);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes an income record by id.
     *
     * @param id the id of the income to delete
     * @return true if the record was deleted, false otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteIncome(@PathVariable int id) {
        boolean deleted = incomeService.deleteIncome(id);
        return ResponseEntity.ok(deleted);
    }
}
