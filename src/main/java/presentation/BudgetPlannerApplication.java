package presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"presentation", "repository", "application"})
public class BudgetPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BudgetPlannerApplication.class, args);
    }
}
