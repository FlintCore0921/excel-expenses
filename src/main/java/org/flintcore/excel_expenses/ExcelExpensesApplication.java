package org.flintcore.excel_expenses;

import javafx.application.Application;
import org.flintcore.excel_expenses.applications.ExpensesApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.flintcore.excel_expenses",
        "org.flintcore.excel_expenses.configurations"
})
public class ExcelExpensesApplication {
    public static void main(String[] args) {
        Application.launch(ExpensesApplication.class, args);
    }
}