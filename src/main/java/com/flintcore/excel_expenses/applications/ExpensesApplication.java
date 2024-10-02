package com.flintcore.excel_expenses.applications;

import com.flintcore.excel_expenses.ExcelExpensesApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;

@Component
public class ExpensesApplication extends Application {
    private ConfigurableApplicationContext springApplicationContext;

    @Override
    public void init()  {
        this.springApplicationContext = new SpringApplicationBuilder(ExcelExpensesApplication.class)
                .run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL resource = Objects.requireNonNull(
                getClass().getResource("/templates/MainView.fxml")
        );
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(springApplicationContext::getBean);

        Scene rootScene = new Scene(loader.load());

        stage.setScene(rootScene);
        stage.setTitle("Expenses");

        stage.show();
    }

    @Override
    public void stop() {
        this.springApplicationContext.close();
    }
}
