package org.flintcore.excel_expenses.applications;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.ExcelExpensesApplication;
import org.flintcore.excel_expenses.listeners.WindowRelocationHandler;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.models.locations.RelocationParam;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URL;
import java.util.Objects;

import static org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.*;

@Component
@RequiredArgsConstructor
public class ExpensesApplication extends Application {
    private ConfigurableApplicationContext springApplicationContext;

    @Override
    public void init() {
        this.springApplicationContext = new SpringApplicationBuilder(
                ExcelExpensesApplication.class
        ).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL resource = Objects.requireNonNull(
                getClass().getResource("/templates/main-view.fxml")
        );

        CompoundResourceBundle resources = this.springApplicationContext
                .getBean(CompoundResourceBundle.class);

        resources.registerBundle(Bundles.GENERAL);

        FXMLLoader loader = new FXMLLoader(resource, resources);
        loader.setControllerFactory(springApplicationContext::getBean);

        Scene rootScene = new Scene(loader.load());

//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(rootScene);
        stage.setTitle(resources.getString("app.title"));

        setListeners(stage);

        getShutdownHandler().setStage(stage);

        stage.show();
    }

    private ShutdownFXApplication getShutdownHandler() {
        return this.springApplicationContext.getBean(ShutdownFXApplication.class);
    }

    private void setListeners(Stage stage) {
        RelocationParam params = new RelocationParam(
                new Dimension(Integer.MAX_VALUE, 15), stage
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
