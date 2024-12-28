package org.flintcore.excel_expenses.managers.factories.navigation;

import javafx.scene.Node;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.managers.routers.factories.nodes.FXMLFactory;
import org.flintcore.excel_expenses.models.NodeWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class NavbarItemFactory {

    private final FXMLFactory fxmlFactory;

    /**@throws IOException if file or buildOf process fails.*/
    public NodeWrapper<? extends Node, ?> createSidebarItem(String fxmlLocation) throws IOException {
        return fxmlFactory.buildLoader(fxmlLocation)
                .orElseThrow(IOException::new);
    }
}
