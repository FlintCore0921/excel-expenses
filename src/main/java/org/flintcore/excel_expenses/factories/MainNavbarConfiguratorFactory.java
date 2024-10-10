package org.flintcore.excel_expenses.factories;

import org.flintcore.excel_expenses.controllers.MenuItemController;
import org.flintcore.excel_expenses.handlers.routers.IRoute;
import org.flintcore.excel_expenses.models.NodeWrapper;
import javafx.scene.Node;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MainNavbarConfiguratorFactory {
    private final NavbarItemFactory navbarItemFactory;

    private static final String MAIN_NAVBAR_ITEM_FXML = "/templates/navbarItem.fxml";

    @SuppressWarnings("unchecked")
    public Supplier<NodeWrapper<Node, MenuItemController>> getNodeViewBuilder() {
        return () -> {
            NodeWrapper<Node, MenuItemController> sidebarItem = null;
            try {
                sidebarItem = (NodeWrapper<Node, MenuItemController>)
                        this.navbarItemFactory.createSidebarItem(MAIN_NAVBAR_ITEM_FXML);
            } catch (IOException | ClassCastException ignored) {
            }

            return sidebarItem;
        };
    }

    public Map<IRoute, NodeWrapper<Node, MenuItemController>> buildMainNavbarItems(
            List<? extends IRoute> routes,
            Consumer<IRoute> routeTrigger
    ) {
        Supplier<NodeWrapper<Node, MenuItemController>> nodeViewBuilder = getNodeViewBuilder();

        return routes.stream()
                .map(rt -> {
                    NodeWrapper<Node, MenuItemController> nodeWrapper = nodeViewBuilder.get();

                    MenuItemController controller = nodeWrapper.controller();

                    controller.setText(
                            StringUtils.capitalize(rt.getName().toLowerCase())
                    );

                    controller.setOnTrigger(e -> routeTrigger.accept(rt));

                    return Map.entry(rt, nodeWrapper);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (old, curr) -> old,
                        LinkedHashMap::new
                ));
    }
}
