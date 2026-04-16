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
		assertThrows(IllegalArgumentException.class, () -> budgetRepository.queryAllRows(null), "Should throw IllegalArgumentException for null table name");
	}

	@Test
	void testQueryAllRowsWithEmptyTableNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> budgetRepository.queryAllRows(""), "Should throw IllegalArgumentException for empty table name");
	}
}
