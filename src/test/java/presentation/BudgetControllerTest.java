package presentation;

import application.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for BudgetController.
 * Verifies that budget summary requests are handled correctly.
 */
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    private BudgetController budgetController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        budgetController = new BudgetController(budgetService);
    }

    @Test
    void testGetSummary() {
        when(budgetService.calculateTotalIncome()).thenReturn(1000.0);
        when(budgetService.calculateTotalExpenses()).thenReturn(400.0);
        when(budgetService.calculateRemainingBudget()).thenReturn(600.0);

        ResponseEntity<Map<String, Double>> response = budgetController.getSummary();

        assertEquals(200, response.getStatusCode().value());
        Map<String, Double> body = response.getBody();
        assert body != null;
        assertEquals(1000.0, body.get("totalIncome"));
        assertEquals(400.0, body.get("totalExpenses"));
        assertEquals(600.0, body.get("remainingBudget"));

        verify(budgetService).calculateTotalIncome();
        verify(budgetService).calculateTotalExpenses();
        verify(budgetService).calculateRemainingBudget();
    }
}
