package application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.IncomeRepository;
import java.util.Map;

/**
 * Service for income-related business logic.
 * Delegates to IncomeRepository for database operations.
 */
@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    /**
     * Adds a new income record.
     *
     * @param income a map containing the income data
     * @return the generated id of the new income
     */
    @Transactional
    public int addIncome(Map<String, Object> income) {
        return incomeRepository.addIncome(income);
    }

    /**
     * Updates an existing income record.
     *
     * @param income a map containing the updated income data
     * @return true if the update was successful, false otherwise
     */
    @Transactional
    public boolean updateIncome(Map<String, Object> income) {
        return incomeRepository.updateIncome(income);
    }

    /**
     * Deletes an income record by id.
     *
     * @param id the id of the income to delete
     * @return true if the record was deleted, false otherwise
     */
    @Transactional
    public boolean deleteIncome(int id) {
        return incomeRepository.deleteIncome(id);
    }
}
