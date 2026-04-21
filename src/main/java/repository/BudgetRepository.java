package repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BudgetRepository {

	/**
	 * Retrieves a single row by ID from the specified table.
	 * @param tableName the name of the table to query
	 * @param id the ID value to search for (assumes column is named 'id')
	 * @return a row as Map<String, Object>, or null if not found
	 * @throws IllegalArgumentException if tableName is null or empty
	 * @throws RuntimeException if the query fails (e.g., table does not exist)
	 */
	public Map<String, Object> queryById(String tableName, int id) {
		if (tableName == null || tableName.trim().isEmpty()) {
			throw new IllegalArgumentException("Table name must not be null or empty");
		}
		String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, id);
		return results.isEmpty() ? null : results.get(0);
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Retrieves all rows from the specified table.
	 * @param tableName the name of the table to query
	 * @return a list of rows (each row is a Map<String, Object>)
	 * @throws IllegalArgumentException if tableName is null or empty
	 * @throws RuntimeException if the query fails (e.g., table does not exist)
	 */
	public List<Map<String, Object>> queryAllRows(String tableName) {
		if (tableName == null || tableName.trim().isEmpty()) {
			throw new IllegalArgumentException("Table name must not be null or empty");
		}
		String sql = "SELECT * FROM " + tableName;
		return jdbcTemplate.queryForList(sql);
	}

    
}