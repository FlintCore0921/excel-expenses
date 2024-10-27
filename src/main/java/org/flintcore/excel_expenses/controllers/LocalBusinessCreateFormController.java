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
import org.flintcore.excel_expenses.models.alerts.ObservableTaskAlert;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableTask;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileFXService;
import org.flintcore.excel_expenses.tasks.bussiness.local.RegisterLocalBusinessOnFileTask;
import org.flintcore.utilities.fx.BindingsUtils;
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

    // Bundles
    private ResourceBundle bundles;

    private ObservableTask<Void> registerTask;

    public LocalBusinessCreateFormController(ApplicationRouter appRouter, LocalBusinessFileFXService localBusinessFileService, LocalBusinessValidator localBusinessValidator) {
        this.appRouter = appRouter;
        this.localBusinessFileService = localBusinessFileService;
        this.localBusinessValidator = localBusinessValidator;
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

        this.btnSave.setOnAction(e -> {
            if (Objects.nonNull(this.registerTask) && this.registerTask.isRunning()) return;

            this.requestRegisterLocalBusinessTask();
        });
    }

    private void requestRegisterLocalBusinessTask() {
        registerTask = new RegisterLocalBusinessOnFileTask(this.localBusinessFileService, this.localBusinessValidator, this.localNameTxt::getText, this.localRNCTxt::getText);

        ObservableTaskAlert<Void> taskAlert = new ObservableTaskAlert<>(registerTask);
        taskAlert.setContentText("Saving...");

        //at moment to complete, Show an alert.
        taskAlert.setOnCompleted(alert -> {
            alert.setTitle(this.bundles.getString("messages.task-completed"));
            alert.setContentText(this.bundles.getString("local.message.save-success"));
            alert.getButtonTypes().setAll(ButtonType.OK);

            PauseTransition onTimeout = new PauseTransition(Duration.seconds(6));
            onTimeout.setOnFinished(__0 -> alert.close());

            onTimeout.play();
        });

        // Clean data
        registerTask.addSubscription(TaskFxEvent.ALL_WORKER_STATE_DONE, () -> {
            List.of(this.localRNCTxt, this.localNameTxt).forEach(TextInputControl::clear);
            this.registerTask = null;
        });

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
