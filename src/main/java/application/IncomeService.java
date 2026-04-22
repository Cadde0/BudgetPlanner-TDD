package application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.IncomeRepository;
import java.util.Map;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Transactional
    public int addIncome(Map<String, Object> income) {
        return incomeRepository.addIncome(income);
    }

    @Transactional
    public boolean updateIncome(Map<String, Object> income) {
        return incomeRepository.updateIncome(income);
    }

    @Transactional
    public boolean deleteIncome(int id) {
        return incomeRepository.deleteIncome(id);
    }
}
