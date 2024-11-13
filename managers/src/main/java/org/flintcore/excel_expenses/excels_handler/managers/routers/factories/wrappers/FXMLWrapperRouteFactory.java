package org.flintcore.excel_expenses.excels_handler.managers.routers.factories.wrappers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.excels_handler.managers.routers.IRoute;
import org.flintcore.excel_expenses.excels_handler.managers.routers.factories.FXMLRouteFactoryImpl;
import org.flintcore.excel_expenses.excels_handler.models.NodeWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Class built to create and prepare {@link FXMLLoader} to create a view.
 */
@Log4j2
@Component
public final class FXMLWrapperRouteFactory extends FXMLRouteFactoryImpl<IRoute, NodeWrapper<Node, ?>> {

    public FXMLWrapperRouteFactory(ApplicationContext appContext, CompoundResourceBundle bundleManager) {
        super(appContext, bundleManager);
    }

    public Optional<NodeWrapper<Node, ?>> buildLoader(IRoute route) {
        try {
            FXMLLoader loader = prepareLoader(route);

            return Optional.of(buildWrapperResult(loader));
        } catch (IOException e) {
            log.error("Fail at load fxml file {}. \nError {}",
                    route.name(), e.getMessage());
        } catch (NullPointerException e) {
            log.error("Fail at load fxml file {}. Invalid route: {}",
                    route.name(), route.getRoute());
        }
        return Optional.empty();
    }
}
