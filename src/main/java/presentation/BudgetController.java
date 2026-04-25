package presentation;

import application.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for general budget calculations and summaries.
 */
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Returns a summary of total income, total expenses and remaining budget.
     *
     * @return map with summary values
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary() {
        return ResponseEntity.ok(Map.of(
                "totalIncome", budgetService.calculateTotalIncome(),
                "totalExpenses", budgetService.calculateTotalExpenses(),
                "remainingBudget", budgetService.calculateRemainingBudget()));
    }
}
