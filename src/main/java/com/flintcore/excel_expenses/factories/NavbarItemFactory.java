package com.flintcore.excel_expenses.factories;

import com.flintcore.excel_expenses.models.NodeWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NavbarItemFactory {

    private final ApplicationContext springContext;

    public <T extends Node> NodeWrapper<T, ?> createSidebarItem(String fxmlLocation) throws IOException {
        URL resource = Objects.requireNonNull(
                getClass().getResource(fxmlLocation)
        );
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(springContext::getBean);

        return new NodeWrapper<>(loader.load(), loader.getController());
    }
}
