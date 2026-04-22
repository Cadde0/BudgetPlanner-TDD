package repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;

/**
 * Repository for category persistence operations.
 *
 * Supports updating a category by ID and deleting categories by ID.
 */
@Repository
public class CategoryRepository {
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String CATEGORY_LIMIT_KEY = "category_limit";
    private static final String CATEGORY_TABLE = "category";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new repository using the provided JDBC template.
     *
     * @param jdbcTemplate JDBC access helper
     */
    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Inserts a new category.
     *
     * @param category input map containing name
     * @return generated category id
     * @throws NullPointerException     if {@code category} is null
     * @throws IllegalArgumentException if required keys are missing
     */
    public int addCategory(Map<String, Object> category) {
        Objects.requireNonNull(category, "Category map must not be null");
        String nameValue = extractName(category);

        if (category.containsKey(CATEGORY_LIMIT_KEY) && category.get(CATEGORY_LIMIT_KEY) != null) {
            Object limitValue = category.get(CATEGORY_LIMIT_KEY);
            String insertSql = "INSERT INTO " + CATEGORY_TABLE
                    + " (" + NAME_KEY + ", " + CATEGORY_LIMIT_KEY + ") VALUES (?, ?)";
            jdbcTemplate.update(insertSql, nameValue, limitValue);
        } else {
            String insertSql = "INSERT INTO " + CATEGORY_TABLE + " (" + NAME_KEY + ") VALUES (?)";
            jdbcTemplate.update(insertSql, nameValue);
        }

        String idSql = "SELECT id FROM " + CATEGORY_TABLE + " WHERE " + NAME_KEY + " = ? ORDER BY id DESC LIMIT 1";
        Integer id = jdbcTemplate.queryForObject(idSql, Integer.class, nameValue);
        if (id == null) {
            throw new IllegalStateException("Failed to create category row");
        }
        return id;
    }

    /**
     * Updates an existing category, identified by its ID.
     *
     * The input map must contain:
     * - {@code id}: the category ID
     * - {@code name}: the new category name/value
     *
     * @param category input map containing id and name
     * @return {@code true} if at least one row was updated, otherwise {@code false}
     * @throws NullPointerException     if {@code category} is null
     * @throws IllegalArgumentException if required keys are missing
     */
    public boolean updateCategory(Map<String, Object> category) {
        Objects.requireNonNull(category, "Category map must not be null");
        int id = requireId(category);
        String nameValue = extractName(category);

        int rows;
        if (category.containsKey(CATEGORY_LIMIT_KEY) && category.get(CATEGORY_LIMIT_KEY) != null) {
            Object limitValue = category.get(CATEGORY_LIMIT_KEY);
            String sql = "UPDATE " + CATEGORY_TABLE
                    + " SET " + NAME_KEY + " = ?, " + CATEGORY_LIMIT_KEY + " = ?"
                    + " WHERE id = ?";
            rows = jdbcTemplate.update(sql, nameValue, limitValue, id);
        } else {
            String sql = "UPDATE " + CATEGORY_TABLE
                    + " SET " + NAME_KEY + " = ?"
                    + " WHERE id = ?";
            rows = jdbcTemplate.update(sql, nameValue, id);
        }

        return rows > 0;
    }

    /**
     * Deletes a category by ID.
     *
     * @param id category ID
     * @return {@code true} if a row was deleted, otherwise {@code false}
     */
    public boolean deleteCategory(int id) {
        if (id <= 0) {
            return false;
        }

        String sql = "DELETE FROM " + CATEGORY_TABLE + " WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    /**
     * Reads and validates the {@code id} value from input.
     */
    private int requireId(Map<String, Object> category) {
        if (!category.containsKey(ID_KEY) || category.get(ID_KEY) == null) {
            throw new IllegalArgumentException("Category must contain 'id'");
        }
        Object idValue = category.get(ID_KEY);
        if (idValue instanceof Integer) {
            return (Integer) idValue;
        }
        if (idValue instanceof Number) {
            return ((Number) idValue).intValue();
        }
        throw new IllegalArgumentException("Category 'id' must be a number");
    }

    /**
     * Extracts the category name/value from {@code name}.
     */
    private String extractName(Map<String, Object> category) {
        Object nameValue = category.get(NAME_KEY);
        if (nameValue != null) {
            return nameValue.toString();
        }

        throw new IllegalArgumentException("Category must contain 'name'");
    }
}
