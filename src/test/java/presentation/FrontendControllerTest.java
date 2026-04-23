package presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BudgetPlannerApplication.class)
@AutoConfigureMockMvc
class FrontendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHomePageContainsAddExpenseUi() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Add Expense")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"expense-form\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"expense-amount\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"expense-description\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"expense-category\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"expense-id\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"expense-update-btn\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"expense-cancel-btn\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("categoryId")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/expenses")));
    }
}
