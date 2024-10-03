package com.flintcore.excel_expenses.factories;

import com.flintcore.excel_expenses.controllers.MenuItemController;
import com.flintcore.excel_expenses.handlers.routers.EMainRoute;
import com.flintcore.excel_expenses.models.NodeWrapper;
import javafx.scene.Node;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    public Map<EMainRoute, NodeWrapper<Node, MenuItemController>> buildMainNavbarItems(
            List<EMainRoute> routes,
            Consumer<EMainRoute> routeTrigger
    ) {
        Supplier<NodeWrapper<Node, MenuItemController>> nodeViewBuilder = getNodeViewBuilder();

        return routes.stream()
                .map(rt -> {
                    NodeWrapper<Node, MenuItemController> nodeWrapper = nodeViewBuilder.get();

                    MenuItemController controller = nodeWrapper.controller();

                    controller.setText(
                            StringUtils.capitalize(rt.name())
                    );

                    controller.setOnTrigger(e -> routeTrigger.accept(rt));

                    return Map.entry(rt, nodeWrapper);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
