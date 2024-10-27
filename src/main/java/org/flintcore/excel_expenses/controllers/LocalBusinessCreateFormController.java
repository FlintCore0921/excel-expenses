package org.flintcore.excel_expenses.controllers;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.managers.rules.ILocalBusinessRules;
import org.flintcore.excel_expenses.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.models.alerts.CancelableTaskAlert;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableTask;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileFXService;
import org.flintcore.excel_expenses.tasks.bussiness.local.RegisterLocalBusinessOnFileTask;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

@Component
@Scope("prototype")
public class LocalBusinessCreateFormController implements Initializable {
    private final ApplicationRouter appRouter;
    @Lazy
    private final LocalBusinessFileFXService localBusinessFileService;
    @Lazy
    private final LocalBusinessValidator localBusinessValidator;

    private ObservableTask<Void> registerTask;

    public LocalBusinessCreateFormController(
            ApplicationRouter appRouter,
            LocalBusinessFileFXService localBusinessFileService,
            LocalBusinessValidator localBusinessValidator) {
        this.appRouter = appRouter;
        this.localBusinessFileService = localBusinessFileService;
        this.localBusinessValidator = localBusinessValidator;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnBack.setOnMousePressed(evt -> this.appRouter.navigateBack());

        configureNameTextField();
        configureSaveLogic();
    }

    private void configureNameTextField() {
        // TODO FIX THIS
        this.localRNCTxt.addEventFilter(KeyEvent.KEY_TYPED, evt -> {
            String content = localRNCTxt.getText();
            if (content.length() >= ILocalBusinessRules.RNC_SIZE
                    || !evt.getCharacter().matches("[0-9]")) {
                evt.consume();
            }
        });

        // TODO FIX THIS
        this.localNameTxt.addEventFilter(KeyEvent.KEY_TYPED, evt -> {
            String content = localNameTxt.getText();
            if (content.length() >= ILocalBusinessRules.MAX_NAME_SIZE) {
                evt.consume();
            }
        });
    }


    private void configureSaveLogic() {
        BooleanBinding disableOnSaveIf = Stream.of(
                Bindings.createBooleanBinding(
                        () -> Stream.of(this.localNameTxt, this.localRNCTxt)
                                .map(TextField::getText)
                                .anyMatch(String::isBlank),
                        this.localRNCTxt.textProperty(), this.localNameTxt.textProperty()
                ),
                Bindings.notEqual(
                        ILocalBusinessRules.RNC_SIZE, this.localRNCTxt.lengthProperty()
                )
        ).reduce(Bindings::or).get();

        this.btnSave.disableProperty().bind(disableOnSaveIf);

        this.btnSave.setOnAction(e -> {
            if (Objects.nonNull(this.registerTask)) return;

            this.requestRegisterLocalBusinessTask();
        });
    }

    private void requestRegisterLocalBusinessTask() {
        registerTask = new RegisterLocalBusinessOnFileTask(
                this.localBusinessFileService, this.localBusinessValidator,
                this.localNameTxt::getText, this.localRNCTxt::getText
        );

        new CancelableTaskAlert(registerTask);

        // Show an alert.
        registerTask.addSubscription(
                TaskFxEvent.WORKER_STATE_SUCCEEDED,
                () -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Task completed");
                    alert.getButtonTypes().retainAll(ButtonType.OK);
                    alert.show();

                    new PauseTransition(Duration.seconds(10))
                            .setOnFinished(__ -> {
                                alert.close();
                            });
                }
        );

        // Clean data
        registerTask.addSubscription(
                TaskFxEvent.ALL_WORKER_STATE_DONE,
                () -> {
                    List.of(this.localRNCTxt, this.localNameTxt)
                            .forEach(TextInputControl::clear);
                    this.registerTask = null;
                }
        );

        new Thread(registerTask).start();
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
