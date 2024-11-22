package org.flintcore.excel_expenses;

import org.flintcore.excel_expenses.applications.ExpensesApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "org.flintcore.excel_expenses"
        }
)
public class ExcelExpensesApplication {
    public static void main(String[] args) {
//		SpringApplication.run(ExcelExpensesApplication.class, args);
        Application.launch(ExpensesApplication.class, args);
    }
}