package presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import repository.BudgetRepository;
import java.util.List;
import java.util.Map;

@RestController
public class DbTestController {
    private final BudgetRepository budgetRepository;

    public DbTestController(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @GetMapping("/dbtest")
    public List<Map<String, Object>> dbTest() {
        // Try querying a table you know exists, e.g., "category"
        return budgetRepository.queryAllRows("category");
    }
}
