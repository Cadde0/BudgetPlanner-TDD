package application;

import org.springframework.stereotype.Service;
import repository.CategoryRepository;

import java.util.Map;

/**
 * Service for category-related operations.
 * Delegates to CategoryRepository for database access.
 * Provides validation for category updates and deletions.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Adds a new category.
     *
     * @param category map containing "name" and optionally "category_limit"
     * @return generated category id
     * @throws NullPointerException     if category map is null
     * @throws IllegalArgumentException if required fields are missing or null
     */
    public int addCategory(Map<String, Object> category) {
        if (category == null) {
            throw new NullPointerException("Category map cannot be null");
        }
        if (!category.containsKey("name") || category.get("name") == null) {
            throw new IllegalArgumentException("Category map must contain 'name'");
        }

        validateOptionalCategoryLimit(category);

        return categoryRepository.addCategory(category);
    }

    /**
     * Updates a category name.
     *
     * @param category map containing "id", "name" and optionally "category_limit"
     * @return true if update succeeded, false if category not found
     * @throws NullPointerException     if category map is null
     * @throws IllegalArgumentException if required fields are missing or null
     */
    public boolean updateCategory(Map<String, Object> category) {
        if (category == null) {
            throw new NullPointerException("Category map cannot be null");
        }
        if (!category.containsKey("id") || category.get("id") == null) {
            throw new IllegalArgumentException("Category map must contain 'id'");
        }
        if (!category.containsKey("name") || category.get("name") == null) {
            throw new IllegalArgumentException("Category map must contain 'name'");
        }

        validateOptionalCategoryLimit(category);

        return categoryRepository.updateCategory(category);
    }

    private void validateOptionalCategoryLimit(Map<String, Object> category) {
        if (!category.containsKey("category_limit") || category.get("category_limit") == null) {
            return;
        }

        Object limit = category.get("category_limit");
        if (!(limit instanceof Number number)) {
            throw new IllegalArgumentException("Category 'category_limit' must be a number");
        }

        if (number.doubleValue() < 0) {
            throw new IllegalArgumentException("Category 'category_limit' must be 0 or greater");
        }
    }

    /**
     * Deletes a category by ID.
     *
     * @param id the category ID
     * @return true if deletion succeeded, false if ID is invalid or category not
     *         found
     */
    public boolean deleteCategory(int id) {
        return categoryRepository.deleteCategory(id);
    }

}
