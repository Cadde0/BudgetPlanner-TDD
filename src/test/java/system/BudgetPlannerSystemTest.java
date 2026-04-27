package system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import presentation.BudgetPlannerApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BudgetPlannerApplication.class)
@AutoConfigureMockMvc
@Transactional
class BudgetPlannerSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void homePageLoadsThroughTheFullSpringContext() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add Expense")))
                .andExpect(content().string(containsString("id=\"expense-form\"")))
                .andExpect(content().string(containsString("/api/expenses")));
    }

    @Test
    void categoryLifecycleWorksThroughHttpApi() throws Exception {
        String categoryName = uniqueName("System Category");

        int categoryId = postForId("/api/categories", Map.of("name", categoryName));
        assertTrue(categoryId > 0);

        List<Map<String, Object>> categoriesAfterCreate = readList(getBody("/api/categories"));
        Map<String, Object> createdCategory = findById(categoriesAfterCreate, categoryId);
        assertNotNull(createdCategory);
        assertCategoryName(createdCategory, categoryName);

        String updatedCategoryName = categoryName + " Updated";
        mockMvc.perform(put("/api/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(Map.of("name", updatedCategoryName))))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        List<Map<String, Object>> categoriesAfterUpdate = readList(getBody("/api/categories"));
        Map<String, Object> updatedCategory = findById(categoriesAfterUpdate, categoryId);
        assertNotNull(updatedCategory);
        assertCategoryName(updatedCategory, updatedCategoryName);

        mockMvc.perform(delete("/api/categories/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        List<Map<String, Object>> categoriesAfterDelete = readList(getBody("/api/categories"));
        assertTrue(findById(categoriesAfterDelete, categoryId) == null);
    }

    @Test
    void incomeLifecycleWorksThroughHttpApi() throws Exception {
        int incomeId = postForId("/api/income", Map.of("amount", 1450));
        assertTrue(incomeId > 0);

        List<Map<String, Object>> incomesAfterCreate = readList(getBody("/api/income"));
        Map<String, Object> createdIncome = findById(incomesAfterCreate, incomeId);
        assertNotNull(createdIncome);
        assertAmount(createdIncome, 1450.0);

        mockMvc.perform(put("/api/income/" + incomeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(Map.of("amount", 1650))))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        List<Map<String, Object>> incomesAfterUpdate = readList(getBody("/api/income"));
        Map<String, Object> updatedIncome = findById(incomesAfterUpdate, incomeId);
        assertNotNull(updatedIncome);
        assertAmount(updatedIncome, 1650.0);

        mockMvc.perform(delete("/api/income/" + incomeId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        List<Map<String, Object>> incomesAfterDelete = readList(getBody("/api/income"));
        assertTrue(findById(incomesAfterDelete, incomeId) == null);
    }

    @Test
    void expenseLifecycleGroupingAndCategoryAssignmentWorkEndToEnd() throws Exception {
        String groceriesCategoryName = uniqueName("Groceries");
        int groceriesCategoryId = postForId("/api/categories", Map.of("name", groceriesCategoryName));
        int travelCategoryId = postForId("/api/categories", Map.of("name", uniqueName("Travel")));

        int expenseId = postForId("/api/expenses", Map.of(
            "amount", 42,
                "description", "Lunch",
                "categoryId", groceriesCategoryId));
        assertTrue(expenseId > 0);

        List<Map<String, Object>> expensesAfterCreate = readList(getBody("/api/expenses"));
        Map<String, Object> createdExpense = findById(expensesAfterCreate, expenseId);
        assertNotNull(createdExpense);
        assertAmount(createdExpense, 42.0);

        List<Map<String, Object>> groupedExpenses = readList(getBody("/api/expenses/grouped"));
        Map<String, Object> groceriesGroup = findGroupByCategoryName(groupedExpenses, groceriesCategoryName);
        assertNotNull(groceriesGroup);
        assertEquals(42.0, toDouble(groceriesGroup.get("total")), 0.001);
        assertEquals(1, ((List<?>) groceriesGroup.get("expenses")).size());

        mockMvc.perform(put("/api/expenses/" + expenseId + "/category/" + travelCategoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        List<Map<String, Object>> expensesAfterReassign = readList(getBody("/api/expenses"));
        Map<String, Object> reassignedExpense = findById(expensesAfterReassign, expenseId);
        assertNotNull(reassignedExpense);
        assertEquals(travelCategoryId, ((Number) reassignedExpense.get("category_id")).intValue());

        mockMvc.perform(delete("/api/expenses/" + expenseId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        List<Map<String, Object>> expensesAfterDelete = readList(getBody("/api/expenses"));
        assertTrue(findById(expensesAfterDelete, expenseId) == null);
    }

    @Test
    void summaryReflectsNewIncomeAndExpenseAmounts() throws Exception {
        Map<String, Double> before = readSummary();

        int categoryId = postForId("/api/categories", Map.of("name", uniqueName("Summary")));
        postForId("/api/income", Map.of("amount", 2000));
        postForId("/api/expenses", Map.of(
                "amount", 350,
                "description", "System summary expense",
                "categoryId", categoryId));

        Map<String, Double> after = readSummary();

        assertEquals(before.get("totalIncome") + 2000.0, after.get("totalIncome"), 0.001);
        assertEquals(before.get("totalExpenses") + 350.0, after.get("totalExpenses"), 0.001);
        assertEquals(before.get("remainingBudget") + 1650.0, after.get("remainingBudget"), 0.001);
    }

    @Test
    void invalidCategoryRequestsAreRejectedByTheHttpLayer() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(Map.of())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please enter a category name."));

        mockMvc.perform(delete("/api/categories/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please choose a valid category."));
    }

    @Test
    void categoriesEndpointReturnsJsonCollection() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private int postForId(String path, Map<String, Object> payload) throws Exception {
        String response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(payload)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return Integer.parseInt(response.trim());
    }

    private String getBody(String path) throws Exception {
        return mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private String asJson(Map<String, Object> payload) throws Exception {
        return objectMapper.writeValueAsString(payload);
    }

    private List<Map<String, Object>> readList(String json) throws Exception {
        return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    private Map<String, Double> readSummary() throws Exception {
        String json = mockMvc.perform(get("/api/budget/summary"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> rawSummary = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        Map<String, Double> summary = new HashMap<>();
        summary.put("totalIncome", toDouble(rawSummary.get("totalIncome")));
        summary.put("totalExpenses", toDouble(rawSummary.get("totalExpenses")));
        summary.put("remainingBudget", toDouble(rawSummary.get("remainingBudget")));
        return summary;
    }

    private Map<String, Object> findById(List<Map<String, Object>> rows, int id) {
        for (Map<String, Object> row : rows) {
            Object value = row.get("id");
            if (value instanceof Number number && number.intValue() == id) {
                return row;
            }
        }
        return null;
    }

    private Map<String, Object> findGroupByCategoryName(List<Map<String, Object>> groups, String categoryName) {
        for (Map<String, Object> group : groups) {
            if (categoryName.equals(group.get("categoryName"))) {
                return group;
            }
        }
        return null;
    }

    private void assertCategoryName(Map<String, Object> category, String expectedName) {
        Object name = category.get("name");
        if (name == null) {
            name = category.get("category");
        }
        assertEquals(expectedName, name);
    }

    private void assertAmount(Map<String, Object> row, double expectedAmount) {
        Object amount = row.get("amount");
        assertNotNull(amount);
        assertEquals(expectedAmount, toDouble(amount), 0.001);
    }

    private double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private String uniqueName(String prefix) {
        return prefix + " " + UUID.randomUUID();
    }
}