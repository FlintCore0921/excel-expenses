package org.flintcore.excel_expenses.managers.routers.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.flintcore.excel_expenses.models.NodeWrapper;

import java.io.IOException;
import java.util.Optional;

public interface IFXMLFactory<L, R> {
    Optional<R> buildLoader(L location);
}
