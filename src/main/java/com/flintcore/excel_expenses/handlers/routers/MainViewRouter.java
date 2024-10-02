package com.flintcore.excel_expenses.handlers.routers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@AllArgsConstructor
public class MainViewRouter {
    private final ApplicationContext applicationContext;

    public void navigateTo(final EMainRoute route, Consumer<Node> paneConsumer) throws IOException {
        URL resource = Objects.requireNonNull(
                getClass().getResource(route.resourceRoute)
        );
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(applicationContext::getBean);

        paneConsumer.accept(loader.load());
    }
}
