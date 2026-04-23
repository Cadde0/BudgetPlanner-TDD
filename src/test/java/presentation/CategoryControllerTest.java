package presentation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BudgetPlannerApplication.class)
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

        @AfterEach
        void cleanUpCreatedCategories() {
                String categoryNameColumn = resolveCategoryNameColumn();
                String matchingCategoryIdsSql = "SELECT id FROM category WHERE " + categoryNameColumn + " LIKE 'TDD %'";

                jdbcTemplate.update("DELETE FROM expenses WHERE category_id IN (" + matchingCategoryIdsSql + ")");
                jdbcTemplate.update("DELETE FROM category WHERE " + categoryNameColumn + " LIKE 'TDD %'");
        }

    @Test
    void testGetAllCategoriesForSelection() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testAddCategoryWithValidName() throws Exception {
        String addJson = "{\"name\":\"TDD New Category\"}";

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson))
                .andExpect(status().isOk());
    }

    @Test
    void testAddCategoryWithValidNameAndCategoryLimit() throws Exception {
        Assumptions.assumeTrue(hasCategoryLimitColumn(),
                "Skipping: category.category_limit column is not present in this schema");

        String addJson = "{\"name\":\"TDD New Category Limit\",\"category_limit\":400}";

        String response = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = Integer.parseInt(response);
        Number limit = jdbcTemplate.queryForObject(
                "SELECT category_limit FROM category WHERE id = ?",
                Number.class,
                id);
        org.junit.jupiter.api.Assertions.assertNotNull(limit);
        org.junit.jupiter.api.Assertions.assertEquals(400, limit.intValue());
    }

    @Test
    void testAddCategoryWithMissingNameShowsUserFriendlyError() throws Exception {
        String addJson = "{}";

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please enter a category name."));
    }

    @Test
    void testUpdateCategoryByIdFromSelectionWithValidName() throws Exception {
        String addJson = "{\"name\":\"TDD Update Source\"}";
        String addResponse = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = Integer.parseInt(addResponse);
        String updateJson = "{\"name\":\"TDD Update Target\"}";

        // The UI should pass a chosen existing category id; user should not manually
        // type ids.
        mockMvc.perform(put("/api/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testUpdateCategoryByIdWithCategoryLimit() throws Exception {
        Assumptions.assumeTrue(hasCategoryLimitColumn(),
                "Skipping: category.category_limit column is not present in this schema");

        String addJson = "{\"name\":\"TDD Limit Source\"}";
        String addResponse = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = Integer.parseInt(addResponse);
        String updateJson = "{\"name\":\"TDD Limit Target\",\"category_limit\":850}";

        mockMvc.perform(put("/api/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        Number limit = jdbcTemplate.queryForObject(
                "SELECT category_limit FROM category WHERE id = ?",
                Number.class,
                id);
        org.junit.jupiter.api.Assertions.assertNotNull(limit);
        org.junit.jupiter.api.Assertions.assertEquals(850, limit.intValue());
    }

    @Test
    void testUpdateCategoryWithMissingNameShowsUserFriendlyError() throws Exception {
        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please enter a new category name."));
    }

    @Test
    void testUpdateCategoryWithUnknownIdShowsUserFriendlyError() throws Exception {
        String updateJson = "{\"name\":\"Any Name\"}";

        mockMvc.perform(put("/api/categories/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Could not find the selected category."));
    }

    @Test
    void testDeleteCategoryBySelectedId() throws Exception {
        String addJson = "{\"name\":\"TDD Delete Source\"}";
        String addResponse = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = Integer.parseInt(addResponse);

        mockMvc.perform(delete("/api/categories/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testDeleteCategoryWithInvalidIdShowsUserFriendlyError() throws Exception {
        mockMvc.perform(delete("/api/categories/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please choose a valid category."));
    }

    @Test
    void testDeleteCategoryWithUnknownIdShowsUserFriendlyError() throws Exception {
        mockMvc.perform(delete("/api/categories/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Could not find the selected category."));
    }

    private boolean hasCategoryLimitColumn() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'category' AND column_name = 'category_limit'",
                Integer.class);
        return count != null && count > 0;
    }

        private String resolveCategoryNameColumn() {
                Integer countName = jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'category' AND column_name = 'name'",
                                Integer.class);
                if (countName != null && countName > 0) {
                        return "name";
                }

                Integer countCategory = jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'category' AND column_name = 'category'",
                                Integer.class);
                if (countCategory != null && countCategory > 0) {
                        return "category";
                }

                throw new IllegalStateException("Expected category table to have either 'name' or 'category' column");
        }
}
