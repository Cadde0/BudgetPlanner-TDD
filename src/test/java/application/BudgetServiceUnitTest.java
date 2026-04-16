package application;
import application.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BudgetService using Mockito to mock database access.
 * These tests verify the logic of getAllFromTable without requiring a real database.
 */
class BudgetServiceUnitTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies that getAllFromTable returns the expected data when the database returns results.
     * Mocks the JdbcTemplate to return a list of rows for the 'category' table.
     */
    @Test
    void testGetAllFromTableReturnsData() {
        java.util.Map<String, Object> row1 = java.util.Collections.singletonMap("col", "val1");
        java.util.Map<String, Object> row2 = java.util.Collections.singletonMap("col", "val2");
        List<java.util.Map<String, Object>> mockResult = Arrays.asList(row1, row2);
        when(jdbcTemplate.queryForList(eq("SELECT * FROM category"))).thenReturn(mockResult);
        List<?> result = budgetService.getAllFromTable("category");
        assertEquals(mockResult, result);
    }

    /**
     * Verifies that getAllFromTable throws IllegalArgumentException when the table name is null.
     */
    @Test
    void testGetAllFromTableNullTableName() {
        assertThrows(IllegalArgumentException.class, () -> budgetService.getAllFromTable(null));
    }

    /**
     * Verifies that getAllFromTable throws IllegalArgumentException when the table name is empty.
     */
    @Test
    void testGetAllFromTableEmptyTableName() {
        assertThrows(IllegalArgumentException.class, () -> budgetService.getAllFromTable(""));
    }

    /**
     * Verifies that getAllFromTable propagates exceptions thrown by the database layer (e.g., table does not exist).
     */
    @Test
    void testGetAllFromTableThrowsOnDbError() {
        when(jdbcTemplate.queryForList(eq("SELECT * FROM nonexistent_table"))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> budgetService.getAllFromTable("nonexistent_table"));
    }
}
