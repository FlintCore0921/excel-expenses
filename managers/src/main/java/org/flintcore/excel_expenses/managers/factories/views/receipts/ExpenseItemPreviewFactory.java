package org.flintcore.excel_expenses.managers.factories.views.receipts;

import javafx.scene.Node;
import org.flintcore.excel_expenses.managers.routers.expenses.EExpenseItemRoute;
import org.flintcore.excel_expenses.managers.routers.factories.wrappers.FXMLWrapperRouteFactory;
import org.flintcore.excel_expenses.models.NodeWrapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExpenseItemPreviewFactory {

    private final FXMLWrapperRouteFactory fxmlFactory;

    public ExpenseItemPreviewFactory(
            FXMLWrapperRouteFactory fxmlFactory
    ) {
        this.fxmlFactory = fxmlFactory;
    }

    @SuppressWarnings("unchecked")
    public <K> Optional<? extends K> buildView() {
        Optional<NodeWrapper<Node, ?>> nodeOptional = fxmlFactory.buildLoader(EExpenseItemRoute.CREATE);
        return nodeOptional.map((NodeWrapper<Node, ?> nodeNodeWrapper) ->
                (K) nodeNodeWrapper.controller()
        );
    }
}
