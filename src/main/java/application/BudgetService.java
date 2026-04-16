package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Retrieves all rows from the specified table.
     * @param tableName the name of the table to query
     * @return a list of rows (each row is a Map<String, Object>)
     * @throws IllegalArgumentException if tableName is null or empty
     * @throws RuntimeException if the query fails (e.g., table does not exist)
     */
    public List<?> getAllFromTable(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name must not be null or empty");
        }
        String sql = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(sql);
    }
}
