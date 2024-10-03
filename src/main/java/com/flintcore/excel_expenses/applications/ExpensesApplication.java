package com.flintcore.excel_expenses.applications;

import com.flintcore.excel_expenses.ExcelExpensesApplication;
import com.flintcore.excel_expenses.listeners.WindowRelocationHandler;
import com.flintcore.excel_expenses.models.locations.RelocationParam;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URL;
import java.util.Objects;

@Component
public class ExpensesApplication extends Application {
    private ConfigurableApplicationContext springApplicationContext;

    @Override
    public void init()  {
        this.springApplicationContext = new SpringApplicationBuilder(
                ExcelExpensesApplication.class
        ).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL resource = Objects.requireNonNull(
                getClass().getResource("/templates/MainView.fxml")
        );
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(springApplicationContext::getBean);

        Scene rootScene = new Scene(loader.load());

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(rootScene);
        stage.setTitle("Expenses");

        setListeners(stage);

        stage.show();
    }

    private static void setListeners(Stage stage) {
        RelocationParam params = new RelocationParam(
                new Dimension(Integer.MAX_VALUE, 15),
                stage
        );
        WindowRelocationHandler windowRelocationHandler =
                new WindowRelocationHandler(params);

        stage.addEventFilter(MouseEvent.ANY, windowRelocationHandler);
    }

    @Override
    public void stop() {
        this.springApplicationContext.close();
    }
}
