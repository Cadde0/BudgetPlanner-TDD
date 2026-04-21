package repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
		Map<String, Object> result = budgetRepository.queryById("category", 1);
		assertNotNull(result, "Should return a row for valid ID");
		assertEquals(1, result.get("id"), "ID should match the requested value");
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
