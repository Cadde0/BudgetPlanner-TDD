package presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import presentation.BudgetPlannerApplication;

@SpringBootTest(classes = BudgetPlannerApplication.class)
@AutoConfigureMockMvc
class IncomeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

        @Test
        void testAddUpdateDeleteIncome() throws Exception {
        // Add income
        String addJson = "{\"amount\":1000}";
        String response = mockMvc.perform(post("/api/income")
            .contentType(MediaType.APPLICATION_JSON)
            .content(addJson))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        int id = Integer.parseInt(response);

        // Update income
        String updateJson = "{\"amount\":1200}";
        mockMvc.perform(put("/api/income/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        // Delete income
        mockMvc.perform(delete("/api/income/" + id))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
        }
}
