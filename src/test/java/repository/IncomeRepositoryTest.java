package repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import presentation.BudgetPlannerApplication;

@SpringBootTest(classes = BudgetPlannerApplication.class)
class IncomeRepositoryTest {

        @Test
        void testAddIncomeWithNullMapThrows() {
            assertThrows(NullPointerException.class, () -> incomeRepository.addIncome(null));
        }

        @Test
        void testAddIncomeWithMissingAmountThrows() {
            Map<String, Object> income = new HashMap<>();
            Exception ex = assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> incomeRepository.addIncome(income));
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }

        @Test
        void testAddIncomeWithZeroAmountThrows() {
            Map<String, Object> income = new HashMap<>();
            income.put("amount", 0);
            Exception ex = assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> incomeRepository.addIncome(income));
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }

        @Test
        void testAddIncomeWithNegativeAmountThrows() {
            Map<String, Object> income = new HashMap<>();
            income.put("amount", -100);
            Exception ex = assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> incomeRepository.addIncome(income));
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }

        @Test
        void testUpdateIncomeWithNullMapThrows() {
            assertThrows(NullPointerException.class, () -> incomeRepository.updateIncome(null));
        }

        @Test
        void testUpdateIncomeWithMissingIdThrows() {
            Map<String, Object> income = new HashMap<>();
            income.put("amount", 100);
            Exception ex = assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> incomeRepository.updateIncome(income));
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }

        @Test
        void testUpdateIncomeWithMissingAmountThrows() {
            Map<String, Object> income = new HashMap<>();
            income.put("id", 1);
            Exception ex = assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> incomeRepository.updateIncome(income));
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }

        @Test
        void testUpdateIncomeWithZeroAmountThrows() {
            Map<String, Object> income = new HashMap<>();
            income.put("id", 1);
            income.put("amount", 0);
            Exception ex = assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> incomeRepository.updateIncome(income));
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }

        @Test
        void testUpdateIncomeWithNegativeAmountThrows() {
            Map<String, Object> income = new HashMap<>();
            income.put("id", 1);
            income.put("amount", -100);
            Exception ex = assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> incomeRepository.updateIncome(income));
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }

        @Test
        void testUpdateIncomeWithNonExistentIdReturnsFalse() {
            Map<String, Object> income = new HashMap<>();
            income.put("id", -9999);
            income.put("amount", 100);
            boolean updated = incomeRepository.updateIncome(income);
            assertFalse(updated);
        }

        @Test
        void testDeleteIncomeWithNonExistentIdReturnsFalse() {
            boolean deleted = incomeRepository.deleteIncome(-9999);
            assertFalse(deleted);
        }

        @Test
        void testDeleteIncomeWithNegativeIdReturnsFalse() {
            boolean deleted = incomeRepository.deleteIncome(-1);
            assertFalse(deleted);
        }
    @Autowired
    private IncomeRepository incomeRepository;


    @Test
    void testAddIncome() {
        Map<String, Object> income = new HashMap<>();
        income.put("amount", 1000);
        int id = incomeRepository.addIncome(income);
        assertTrue(id > 0);
    }


    @Test
    void testUpdateIncome() {
        // First, add an income to get a valid id
        Map<String, Object> newIncome = new HashMap<>();
        newIncome.put("amount", 1000);
        int id = incomeRepository.addIncome(newIncome);
        // Now update the income
        Map<String, Object> income = new HashMap<>();
        income.put("id", id);
        income.put("amount", 1200);
        boolean updated = incomeRepository.updateIncome(income);
        assertTrue(updated);
    }

    @Test
    void testDeleteIncome() {
        // First, add an income to get a valid id
        Map<String, Object> newIncome = new HashMap<>();
        newIncome.put("amount", 1000);
        int id = incomeRepository.addIncome(newIncome);
        // Now delete the income
        boolean deleted = incomeRepository.deleteIncome(id);
        assertTrue(deleted);
    }
}
