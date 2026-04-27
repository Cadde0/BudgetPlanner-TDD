package repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import presentation.BudgetPlannerApplication;
/**
 * Integration tests for repository layer database access using the real database.
 * These tests use Spring Boot context and do not mock the data.
 */
@SpringBootTest(classes = BudgetPlannerApplication.class)
class BudgetRepositoryTests {

	@Autowired
	private BudgetRepository budgetRepository;

	@Autowired
    private JdbcTemplate jdbcTemplate;

	@Test
	void testQueryAllRowsFromCategoryTable() {
		List<Map<String, Object>> result = budgetRepository.queryAllRows("category");
		assertNotNull(result, "Result for 'category' should not be null");
	}

	@Test
	void testQueryAllRowsFromExpensesTable() {
		List<Map<String, Object>> result = budgetRepository.queryAllRows("expenses");
		assertNotNull(result, "Result for 'expenses' should not be null");
	}

	@Test
	void testQueryAllRowsFromIncomeTable() {
		List<Map<String, Object>> result = budgetRepository.queryAllRows("income");
		assertNotNull(result, "Result for 'income' should not be null");
	}

	@Test
	void testQueryAllRowsFromNonExistentTableThrows() {
		assertThrows(Exception.class, () -> budgetRepository.queryAllRows("nonexistent_table"), "Should throw when table does not exist");
	}

	@Test
    void testQueryAllRowsWithNullTableNameThrows() {
        assertThrows(RuntimeException.class, () -> budgetRepository.queryAllRows(null), "Should throw for null table name");
    }

	@Test
    void testQueryAllRowsWithEmptyTableNameThrows() {
        assertThrows(RuntimeException.class, () -> budgetRepository.queryAllRows(""), "Should throw for empty table name");
    }

	@Test
	void testQueryByIdReturnsRowForValidId() {
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	jdbcTemplate.update(connection -> {
        	PreparedStatement ps = connection.prepareStatement(
         	"INSERT INTO category (name, category_limit, description) VALUES (?, ?, ?)",
            new String[]{"id"}  // ← specify only the key column
        );
        	ps.setString(1, "Test Category");
        	ps.setInt(2, 100);
        	ps.setString(3, "Test Description");
        	return ps;
    }, 	keyHolder);

    int insertedId = keyHolder.getKey().intValue();

    Map<String, Object> result = budgetRepository.queryById("category", insertedId);
    assertNotNull(result, "Should return a row for valid ID");
    assertEquals(insertedId, result.get("id"), "ID should match the requested value");
}

	@Test
	void testQueryByIdReturnsNullForNonExistentId() {
		Map<String, Object> result = budgetRepository.queryById("category", -9999);
		assertNull(result, "Should return null for non-existent ID");
	}

	@Test
	void testQueryByIdWithNullTableNameThrows() {
		assertThrows(RuntimeException.class, () -> budgetRepository.queryById(null, 1), "Should throw for null table name");
	}

	@Test
	void testQueryByIdWithEmptyTableNameThrows() {
		assertThrows(RuntimeException.class, () -> budgetRepository.queryById("", 1), "Should throw for empty table name");
	}
}
