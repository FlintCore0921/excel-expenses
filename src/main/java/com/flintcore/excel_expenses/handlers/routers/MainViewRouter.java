package com.flintcore.excel_expenses.handlers.routers;

import com.flintcore.excel_expenses.handlers.exceptions.ErrorConsumerHandler;
import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class MainViewRouter {
    private final ApplicationContext applicationContext;

    @Getter
    private final ErrorConsumerHandler errorConsumerHandler;

    public MainViewRouter(ApplicationContext applicationContext, ErrorConsumerHandler errorConsumerHandler) {
        this.applicationContext = applicationContext;
        this.errorConsumerHandler = errorConsumerHandler;
    }

    public void navigateTo(
            final EMainRoute route,
            Consumer<Node> paneConsumer,
            Supplier<Transition> transition
    ) {
        URL resource = Objects.requireNonNull(
                getClass().getResource(route.resourceRoute)
        );

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(applicationContext::getBean);

        try {
            paneConsumer.accept(loader.load());
            NullableUtils.executeNonNull(
                    transition,
                    tr -> tr.get().play()
            );
        } catch (IOException e) {
            this.errorConsumerHandler.accept(e);
        }
    }

    public void navigateTo(final EMainRoute route, Consumer<Node> paneConsume) {
        this.navigateTo(route, paneConsume, null);
    }
}
