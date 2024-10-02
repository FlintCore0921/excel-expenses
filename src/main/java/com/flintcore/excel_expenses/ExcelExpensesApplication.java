package com.flintcore.excel_expenses;

import com.flintcore.excel_expenses.applications.ExpensesApplication;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExcelExpensesApplication {
	public static void main(String[] args) {
//		SpringApplication.run(ExcelExpensesApplication.class, args);
		Application.launch(ExpensesApplication.class, args);
	}	
}
