package org.flintcore.excel_expenses.managers.routers.factories.nodes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.routers.factories.FXMLFactoryImpl;
import org.flintcore.excel_expenses.models.NodeWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Class built to create and prepare {@link FXMLLoader} to create a view.
 */
@Log4j2
@Component
public final class FXMLFactory extends FXMLFactoryImpl<String, NodeWrapper<Node, ?>> {

    public FXMLFactory(ApplicationContext appContext) {
        super(appContext);
    }

    public Optional<NodeWrapper<Node, ?>> buildLoader(String route) {
        try {
            FXMLLoader loader = prepareLoader(route);
            return Optional.of(buildWrapperResult(loader));
        } catch (IOException e) {
            log.error("Fail at load fxml file {}. \nError {}",
                    route, e.getMessage());
        } catch (NullPointerException e) {
            log.error("Fail at load. Invalid route: {}", route);
        }
        return Optional.empty();
    }
}
