package application;

import application.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.IncomeRepository;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncomeServiceUnitTest {
    @Mock
    private IncomeRepository incomeRepository;
    @InjectMocks
    private IncomeService incomeService;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testAddIncome() {
        Map<String, Object> income = new HashMap<>();
        income.put("amount", 1000);
        income.put("description", "Salary");
        when(incomeRepository.addIncome(anyMap())).thenReturn(1);
        int id = incomeService.addIncome(income);
        assertEquals(1, id);
    }

    @Test
    void testUpdateIncome() {
        Map<String, Object> income = new HashMap<>();
        income.put("id", 1);
        income.put("amount", 1200);
        income.put("description", "Updated");
        when(incomeRepository.updateIncome(anyMap())).thenReturn(true);
        boolean updated = incomeService.updateIncome(income);
        assertTrue(updated);
    }

    @Test
    void testDeleteIncome() {
        when(incomeRepository.deleteIncome(1)).thenReturn(true);
        boolean deleted = incomeService.deleteIncome(1);
        assertTrue(deleted);
    }
}
