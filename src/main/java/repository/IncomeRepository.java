package repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Objects;

/**
 * Repository for CRUD operations on the income table.
 * Handles validation and database interaction for income records.
 */
@Repository
public class IncomeRepository {
	private final JdbcTemplate jdbcTemplate;

	public IncomeRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Inserts a new income record into the database.
	 *
	 * @param income a map containing the income data (must include 'amount' > 0)
	 * @return the generated id of the new income
	 * @throws NullPointerException if income is null
	 * @throws IllegalArgumentException if amount is missing or not positive
	 */
	public int addIncome(Map<String, Object> income) {
		Objects.requireNonNull(income, "Income map must not be null");
		if (!income.containsKey("amount")) {
			throw new IllegalArgumentException("Income must contain 'amount'");
		}
		Number amount = (Number) income.get("amount");
		if (amount == null || amount.doubleValue() <= 0) {
			throw new IllegalArgumentException("Income amount must be positive");
		}
		String sql = "INSERT INTO income (amount) VALUES (?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
			ps.setObject(1, amount);
			return ps;
		}, keyHolder);
		Number key = keyHolder.getKey();
		if (key == null) {
			throw new IllegalStateException("Failed to retrieve generated id for income");
		}
		return key.intValue();
	}

	/**
	 * Updates an existing income record in the database.
	 *
	 * @param income a map containing 'id' and new 'amount' (> 0)
	 * @return true if the update was successful, false otherwise
	 * @throws NullPointerException if income is null
	 * @throws IllegalArgumentException if id or amount is missing/invalid
	 */
	public boolean updateIncome(Map<String, Object> income) {
		Objects.requireNonNull(income, "Income map must not be null");
		if (!income.containsKey("id") || !income.containsKey("amount")) {
			throw new IllegalArgumentException("Income must contain 'id' and 'amount'");
		}
		Number amount = (Number) income.get("amount");
		if (amount == null || amount.doubleValue() <= 0) {
			throw new IllegalArgumentException("Income amount must be positive");
		}
		String sql = "UPDATE income SET amount = ? WHERE id = ?";
		int rows = jdbcTemplate.update(sql, amount, income.get("id"));
		return rows > 0;
	}

	/**
	 * Deletes an income record by id.
	 *
	 * @param id the id of the income to delete
	 * @return true if the record was deleted, false otherwise
	 */
	public boolean deleteIncome(int id) {
		String sql = "DELETE FROM income WHERE id = ?";
		int rows = jdbcTemplate.update(sql, id);
		return rows > 0;
	}
}
