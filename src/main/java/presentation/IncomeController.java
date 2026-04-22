package presentation;

import application.IncomeService;
import application.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/income")
public class IncomeController {
    private final IncomeService incomeService;
    private final BudgetService budgetService;

    public IncomeController(IncomeService incomeService, BudgetService budgetService) {
        this.incomeService = incomeService;
        this.budgetService = budgetService;
    }
    @GetMapping
    public List<Map<String, Object>> getAllIncomes() {
        return budgetService.getAllFromTable("income");
    }

    @PostMapping
    public ResponseEntity<Integer> addIncome(@RequestBody Map<String, Object> income) {
        int id = incomeService.addIncome(income);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateIncome(@PathVariable int id, @RequestBody Map<String, Object> income) {
        income.put("id", id);
        boolean updated = incomeService.updateIncome(income);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteIncome(@PathVariable int id) {
        boolean deleted = incomeService.deleteIncome(id);
        return ResponseEntity.ok(deleted);
    }
}
