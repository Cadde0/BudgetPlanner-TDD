package repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import presentation.BudgetPlannerApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = BudgetPlannerApplication.class)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final List<Integer> createdCategoryIds = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (Integer id : createdCategoryIds) {
            jdbcTemplate.update("DELETE FROM category WHERE id = ?", id);
        }
        createdCategoryIds.clear();
    }

    @Test
    void testUpdateCategoryWithNullMapThrows() {
        assertThrows(NullPointerException.class, () -> categoryRepository.updateCategory(null));
    }

    @Test
    void testAddCategoryWithNullMapThrows() {
        assertThrows(NullPointerException.class, () -> categoryRepository.addCategory(null));
    }

    @Test
    void testAddCategoryWithMissingNameThrows() {
        Map<String, Object> category = new HashMap<>();
        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> categoryRepository.addCategory(category));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testAddCategory() {
        String categoryNameColumn = resolveCategoryNameColumn();

        Map<String, Object> category = new HashMap<>();
        category.put("name", "TDD Added");

        int id = categoryRepository.addCategory(category);
        createdCategoryIds.add(id);
        assertTrue(id > 0);

        String sql = "SELECT " + categoryNameColumn + " FROM category WHERE id = ?";
        String value = jdbcTemplate.queryForObject(sql, String.class, id);
        assertEquals("TDD Added", value);
    }

    @Test
    void testUpdateCategoryWithMissingIdThrows() {
        Map<String, Object> category = new HashMap<>();
        category.put("name", "Food");
        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> categoryRepository.updateCategory(category));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testUpdateCategoryWithMissingNameThrows() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        Exception ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> categoryRepository.updateCategory(category));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testUpdateCategoryWithNonExistentIdReturnsFalse() {
        Map<String, Object> category = new HashMap<>();
        category.put("id", -9999);
        category.put("name", "TDD Updated");
        category.put("category", "TDD Updated");

        boolean updated = categoryRepository.updateCategory(category);
        assertFalse(updated);
    }

    @Test
    void testUpdateCategory() {
        String categoryNameColumn = resolveCategoryNameColumn();
        int id = createTempCategory(categoryNameColumn, "TDD Original");

        Map<String, Object> category = new HashMap<>();
        category.put("id", id);
        category.put("name", "TDD Updated");
        category.put("category", "TDD Updated");

        boolean updated = categoryRepository.updateCategory(category);
        assertTrue(updated);

        String sql = "SELECT " + categoryNameColumn + " FROM category WHERE id = ?";
        String value = jdbcTemplate.queryForObject(sql, String.class, id);
        assertEquals("TDD Updated", value);
    }

    @Test
    void testUpdateCategoryWithLimit() {
        String categoryNameColumn = resolveCategoryNameColumn();
        Assumptions.assumeTrue(hasCategoryLimitColumn(),
                "Skipping: category.category_limit column is not present in this schema");
        String categoryLimitColumn = "category_limit";
        int id = createTempCategory(categoryNameColumn, "TDD Amount Original");

        Map<String, Object> category = new HashMap<>();
        category.put("id", id);
        category.put("name", "TDD Amount Updated");
        category.put("category_limit", 250);

        boolean updated = categoryRepository.updateCategory(category);
        assertTrue(updated);

        String amountSql = "SELECT " + categoryLimitColumn + " FROM category WHERE id = ?";
        Number limit = jdbcTemplate.queryForObject(amountSql, Number.class, id);
        assertNotNull(limit);
        assertEquals(250, limit.intValue());
    }

    @Test
    void testUpdateCategoryWithCategoryLimitAndNonExistentIdReturnsFalse() {
        Assumptions.assumeTrue(hasCategoryLimitColumn(),
                "Skipping: category.category_limit column is not present in this schema");

        Map<String, Object> category = new HashMap<>();
        category.put("id", -9999);
        category.put("name", "TDD Amount Updated");
        category.put("category_limit", 999);

        boolean updated = categoryRepository.updateCategory(category);
        assertFalse(updated);
    }

    @Test
    void testDeleteCategoryWithNonExistentIdReturnsFalse() {
        boolean deleted = categoryRepository.deleteCategory(-9999);
        assertFalse(deleted);
    }

    @Test
    void testDeleteCategoryWithNegativeIdReturnsFalse() {
        boolean deleted = categoryRepository.deleteCategory(-1);
        assertFalse(deleted);
    }

    @Test
    void testDeleteCategory() {
        String categoryNameColumn = resolveCategoryNameColumn();
        int id = createTempCategory(categoryNameColumn, "TDD Delete");

        boolean deleted = categoryRepository.deleteCategory(id);
        assertTrue(deleted);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM category WHERE id = ?",
                Integer.class,
                id);
        assertNotNull(count);
        assertEquals(0, count.intValue());

        createdCategoryIds.remove(Integer.valueOf(id));
    }

    private String resolveCategoryNameColumn() {
        Integer countName = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'category' AND column_name = 'name'",
                Integer.class);
        if (countName != null && countName > 0) {
            return "name";
        }

        Integer countCategory = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'category' AND column_name = 'category'",
                Integer.class);
        if (countCategory != null && countCategory > 0) {
            return "category";
        }

        throw new IllegalStateException("Expected category table to have either 'name' or 'category' column");
    }

    private boolean hasCategoryLimitColumn() {
        Integer countAmount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'category' AND column_name = 'category_limit'",
                Integer.class);
        return countAmount != null && countAmount > 0;
    }

    private int createTempCategory(String categoryNameColumn, String value) {
        String insertSql = "INSERT INTO category (" + categoryNameColumn + ") VALUES (?)";
        jdbcTemplate.update(insertSql, value);

        String idSql = "SELECT id FROM category WHERE " + categoryNameColumn + " = ? ORDER BY id DESC LIMIT 1";
        Integer id = jdbcTemplate.queryForObject(idSql, Integer.class, value);
        if (id == null) {
            throw new IllegalStateException("Failed to create test category row");
        }
        createdCategoryIds.add(id);
        return id;
    }
}
