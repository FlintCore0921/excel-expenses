package org.flintcore.excel_expenses.excels_handler.managers.factories.navigation;

import javafx.scene.Node;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flintcore.excel_expenses.excels_handler.models.NodeWrapper;
import org.flintcore.excel_expenses.excels_handler.managers.routers.IRoute;
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
    public Supplier<NodeWrapper<Node, IMenuItemHandler>> getNodeViewBuilder() {
        return () -> {
            NodeWrapper<Node, IMenuItemHandler> sidebarItem = null;
            try {
                sidebarItem = (NodeWrapper<Node, IMenuItemHandler>)
                        this.navbarItemFactory.createSidebarItem(MAIN_NAVBAR_ITEM_FXML);
            } catch (IOException | ClassCastException ignored) {
            }

            return sidebarItem;
        };
    }

    public Map<IRoute, NodeWrapper<Node, IMenuItemHandler>> buildMainNavbarItems(
            List<? extends IRoute> routes,
            Consumer<IRoute> routeTrigger
    ) {
        Supplier<NodeWrapper<Node, IMenuItemHandler>> nodeViewBuilder = getNodeViewBuilder();

        return routes.stream()
                .map(rt -> {
                    NodeWrapper<Node, IMenuItemHandler> nodeWrapper = nodeViewBuilder.get();

                    IMenuItemHandler controller = nodeWrapper.controller();

                    controller.setText(
                            StringUtils.capitalize(rt.name().toLowerCase())
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
