package presentation;

import application.BudgetService;
import application.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final BudgetService budgetService;

    public CategoryController(CategoryService categoryService, BudgetService budgetService) {
        this.categoryService = categoryService;
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<Map<String, Object>> getAllCategories() {
        return budgetService.getAllFromTable("category");
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody Map<String, Object> category) {
        if (category == null || category.get("name") == null) {
            return badRequest("Please enter a category name.");
        }

        int id = categoryService.addCategory(category);

        // Ensure optional category_limit is persisted for schemas where insert paths
        // only create the base row and updates handle extended fields.
        if (category.containsKey("category_limit") && category.get("category_limit") != null) {
            Map<String, Object> categoryUpdate = new HashMap<>();
            categoryUpdate.put("id", id);
            categoryUpdate.put("name", category.get("name"));
            categoryUpdate.put("category_limit", category.get("category_limit"));
            categoryService.updateCategory(categoryUpdate);
        }

        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable int id, @RequestBody Map<String, Object> category) {
        if (category == null || category.get("name") == null) {
            return badRequest("Please enter a new category name.");
        }

        category.put("id", id);
        boolean updated = categoryService.updateCategory(category);
        if (!updated) {
            return notFound("Could not find the selected category.");
        }

        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        if (id <= 0) {
            return badRequest("Please choose a valid category.");
        }

        boolean deleted = categoryService.deleteCategory(id);
        if (!deleted) {
            return notFound("Could not find the selected category.");
        }

        return ResponseEntity.ok(true);
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<Map<String, String>> notFound(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        return ResponseEntity.status(404).body(body);
    }

}
