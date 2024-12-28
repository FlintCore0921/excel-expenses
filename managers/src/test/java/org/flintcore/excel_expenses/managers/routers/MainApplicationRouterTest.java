package org.flintcore.excel_expenses.managers.routers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.flintcore.excel_expenses.managers.factories.transitions.RouteTransitionNavigationFactory;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.factories.nodes.FXMLRouteFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testfx.framework.junit5.ApplicationTest;

@SpringJUnitConfig(classes = {
        RouterConfiguration.class,
        CompoundResourceBundle.class,
        RouteTransitionNavigationFactory.class,
        FXMLRouteFactory.class,
})
class MainApplicationRouterTest extends ApplicationTest {
    @Autowired
    private MainApplicationRouter mainApplicationRouter;

    private Button btnMain, btnHome, btnExpense;
    private StackPane viewStack;
    private Scene stageScene;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        StackPane stackPane = new StackPane();

        this.btnMain = new Button("Main");
        this.btnMain.setId("main");
        this.btnMain.setOnAction(e -> {
            ObservableList<Node> children = stackPane.getChildren();
            children.clear();
            children.add(new Label("Home 1"));
        });

        this.btnHome = new Button("Home");
        this.btnHome.setId("home");
        this.btnHome.setOnAction(e -> {});

        this.btnExpense = new Button("Expense");
        this.btnExpense.setId("expense");
        this.btnExpense.setOnAction(e -> {});

        // Set views

        viewStack = new StackPane(btnMain, btnHome, btnExpense);
        stageScene = new Scene(new VBox(viewStack, stackPane), 100, 100);
        stage.setScene(stageScene);
        this.mainApplicationRouter.setParentContainer(stackPane);
    }

    @Test
    void shouldMoveToNextRoute() {
        
    }
}