package org.flintcore.excel_expenses.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.managers.rules.ILocalBusinessRules;
import org.flintcore.excel_expenses.models.alerts.ObservableTaskAlert;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.tasks.bussiness.local.StoreLocalBusinessService;
import org.flintcore.utilities.fx.BindingsUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

@Component
@Scope("prototype")
public class LocalBusinessCreateFormController implements Initializable {
    private final ApplicationRouter appRouter;
    @Lazy
    private final StoreLocalBusinessService storeBusinessTaskService;
    // Bundles
    private ResourceBundle bundles;

    public LocalBusinessCreateFormController(
            ApplicationRouter appRouter,
            StoreLocalBusinessService storeBusinessTaskService) {
        this.appRouter = appRouter;
        this.storeBusinessTaskService = storeBusinessTaskService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundles = resources;

        this.btnBack.setOnMousePressed(evt -> this.appRouter.navigateBack());

        configureNameTextField();
        configureSaveLogic();
    }

    private void configureNameTextField() {
        // TODO FIX THIS
        this.localRNCTxt.addEventFilter(KeyEvent.KEY_TYPED, evt -> {
            String content = localRNCTxt.getText();
            if (content.length() >= ILocalBusinessRules.RNC_SIZE || !evt.getCharacter().matches("[0-9]")) {
                evt.consume();
            }
        });

        // TODO FIX THIS
        this.localNameTxt.addEventFilter(KeyEvent.KEY_TYPED, evt -> {
            String content = localNameTxt.getText();
            if (content.length() > ILocalBusinessRules.MAX_NAME_SIZE) {
                evt.consume();
            }
        });
    }


    private void configureSaveLogic() {
        // Disable if Any field is blank, and RNC field not has the expected size.
        TextField[] fieldsToValidate = {this.localNameTxt, this.localRNCTxt};

        BooleanBinding disableOnSaveIf = Stream.of(
                BindingsUtils.createAnyBlankBinding(fieldsToValidate),
                Bindings.notEqual(
                        ILocalBusinessRules.RNC_SIZE,
                        this.localRNCTxt.lengthProperty()
                )
        ).reduce(Bindings::or).orElseThrow();

        this.btnSave.disableProperty().bind(disableOnSaveIf);

        this.storeBusinessTaskService.setLocalRNCSupplier(this.localRNCTxt::getText);
        this.storeBusinessTaskService.setLocalNameSupplier(this.localNameTxt::getText);

        // Clean data in used fields
        this.storeBusinessTaskService.addSubscription(List.of(TaskFxEvent.WORKER_STATE_SUCCEEDED, TaskFxEvent.WORKER_STATE_FAILED), () -> {
            List.of(this.localRNCTxt, this.localNameTxt).forEach(TextInputControl::clear);
        });

        this.btnSave.setOnAction(e -> this.requestRegisterLocalBusinessTask());
    }

    private void requestRegisterLocalBusinessTask() {
        if (storeBusinessTaskService.isRunning()) return;

        ObservableTaskAlert taskAlert = new ObservableTaskAlert(this.storeBusinessTaskService::cancel);

        taskAlert.setTitle(bundles.getString("messages.saving-loading"));
        taskAlert.bindTaskState(this.storeBusinessTaskService.stateProperty());
        taskAlert.bindMessage(this.storeBusinessTaskService.messageProperty());

        this.storeBusinessTaskService.restart();
    }

    @FXML
    private Label btnBack;
    @FXML
    private Button btnSave;

    @FXML
    private TextField localRNCTxt;

    @FXML
    private TextField localNameTxt;

    @FXML
    private BorderPane titleContainer;
}
