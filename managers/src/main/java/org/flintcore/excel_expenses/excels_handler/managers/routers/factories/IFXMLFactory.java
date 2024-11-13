package org.flintcore.excel_expenses.excels_handler.managers.routers.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.flintcore.excel_expenses.excels_handler.models.NodeWrapper;

import java.io.IOException;
import java.util.Optional;

public interface IFXMLFactory<L, R> {
    Optional<R> buildLoader(L location);

    default NodeWrapper<Node, Object> buildWrapperResult(FXMLLoader loader) throws IOException {
        return new NodeWrapper<>(loader.load(), loader.getController());
    }
}
