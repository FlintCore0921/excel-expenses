package org.flintcore.excel_expenses.managers.routers.builders;

import javafx.fxml.FXMLLoader;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.IRoute;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Class built to create and prepare {@link FXMLLoader} to create a view.
 */
@Log4j2
@Component
@AllArgsConstructor
public final class FXMLFactory {
    private final ApplicationContext appContext;
    private CompoundResourceBundle bundleManager;

    public <T> Optional<T> buildLoader(IRoute route) {
        try {
            URL resource = Objects.requireNonNull(
                    getClass().getResource(route.getRoute())
            );

            this.bundleManager.registerBundles(route.getBundlePaths());

            FXMLLoader loader = new FXMLLoader(resource, this.bundleManager);
            loader.setControllerFactory(appContext::getBean);
            return Optional.ofNullable(loader.load());
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
