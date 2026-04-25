package application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.BudgetRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceUnitTest {
    @Mock
    private BudgetRepository budgetRepository;
    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testGetAllFromTableReturnsData() {
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1);
        row1.put("name", "Food");
        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2);
        row2.put("name", "Rent");
        List<Map<String, Object>> mockResult = Arrays.asList(row1, row2);
        when(budgetRepository.queryAllRows("category")).thenReturn(mockResult);
        List<Map<String, Object>> result = budgetService.getAllFromTable("category");
        assertEquals(mockResult, result);
        verify(budgetRepository).queryAllRows("category");
    }

    @Test
    void testGetAllFromTableThrowsOnNull() {
        when(budgetRepository.queryAllRows(null)).thenThrow(new IllegalArgumentException("Table name must not be null or empty"));
        assertThrows(IllegalArgumentException.class, () -> budgetService.getAllFromTable(null));
        verify(budgetRepository).queryAllRows(null);
    }

    @Test
    void testGetAllFromTableThrowsOnEmpty() {
        when(budgetRepository.queryAllRows(" ")).thenThrow(new IllegalArgumentException("Table name must not be null or empty"));
        assertThrows(IllegalArgumentException.class, () -> budgetService.getAllFromTable(" "));
        verify(budgetRepository).queryAllRows(" ");
    }

    @Test
    void testGetAllFromTableThrowsOnDbError() {
        when(budgetRepository.queryAllRows("nonexistent_table")).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> budgetService.getAllFromTable("nonexistent_table"));
        verify(budgetRepository).queryAllRows("nonexistent_table");
    }

    @Test
    void testGetByIdReturnsRow() {
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("name", "Food");
        when(budgetRepository.queryById("category", 1)).thenReturn(row);
        Map<String, Object> result = budgetService.getById("category", 1);
        assertEquals(row, result);
        verify(budgetRepository).queryById("category", 1);
    }

    @Test
    void testGetByIdReturnsNull() {
        when(budgetRepository.queryById("category", 99)).thenReturn(null);
        Map<String, Object> result = budgetService.getById("category", 99);
        assertNull(result);
        verify(budgetRepository).queryById("category", 99);
    }

    @Test
    void testGetByIdThrowsOnNullTable() {
        when(budgetRepository.queryById(null, 1)).thenThrow(new IllegalArgumentException("Table name must not be null or empty"));
        assertThrows(IllegalArgumentException.class, () -> budgetService.getById(null, 1));
        verify(budgetRepository).queryById(null, 1);
    }

    @Test
    void testGetByIdThrowsOnDbError() {
        when(budgetRepository.queryById("category", 1)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> budgetService.getById("category", 1));
        verify(budgetRepository).queryById("category", 1);
    }

    @Test
    void testCalculateTotalExpenses() {
        List<Map<String, Object>> expenses = Arrays.asList(
            Map.of("amount", 100.50),
            Map.of("amount", 50.25)
        );
        when(budgetRepository.queryAllRows("expenses")).thenReturn(expenses);

        double total = budgetService.calculateTotalExpenses();

        assertEquals(150.75, total, 0.001);
    }

    @Test
    void testCalculateTotalExpensesWithEmptyList() {
        when(budgetRepository.queryAllRows("expenses")).thenReturn(Collections.emptyList());
        double total = budgetService.calculateTotalExpenses();
        assertEquals(0.0, total);
    }

    @Test
    void testCalculateTotalIncome() {
        List<Map<String, Object>> income = Arrays.asList(
            Map.of("amount", 1000.0),
            Map.of("amount", 500.0)
        );
        when(budgetRepository.queryAllRows("income")).thenReturn(income);

        double total = budgetService.calculateTotalIncome();

        assertEquals(1500.0, total, 0.001);
    }

    @Test
    void testCalculateTotalIncomeWithEmptyList() {
        when(budgetRepository.queryAllRows("income")).thenReturn(Collections.emptyList());
        double total = budgetService.calculateTotalIncome();
        assertEquals(0.0, total);
    }

    @Test
    void testCalculateRemainingBudget() {
        List<Map<String, Object>> income = List.of(Map.of("amount", 2000.0));
        List<Map<String, Object>> expenses = List.of(
            Map.of("amount", 500.0),
            Map.of("amount", 300.0)
        );
        when(budgetRepository.queryAllRows("income")).thenReturn(income);
        when(budgetRepository.queryAllRows("expenses")).thenReturn(expenses);

        double remaining = budgetService.calculateRemainingBudget();

        assertEquals(1200.0, remaining, 0.001);
    }

    @Test
    void testCalculateRemainingBudgetWithNoData() {
        when(budgetRepository.queryAllRows("income")).thenReturn(Collections.emptyList());
        when(budgetRepository.queryAllRows("expenses")).thenReturn(Collections.emptyList());

        double remaining = budgetService.calculateRemainingBudget();

        assertEquals(0.0, remaining);
    }
}

