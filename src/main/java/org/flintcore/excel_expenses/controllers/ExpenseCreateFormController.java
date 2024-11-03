package org.flintcore.excel_expenses.controllers;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.flintcore.excel_expenses.managers.builders.ReceiptBuilderService;
import org.flintcore.excel_expenses.managers.events.texts.fillers.NFCAutoCompleteListener;
import org.flintcore.excel_expenses.managers.events.texts.filters.business.RNCFilterManager;
import org.flintcore.excel_expenses.managers.pickers.dates.ByMonthCellFactory;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.managers.routers.local.ELocalRoute;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.properties.formatters.StaticNumericFormatter;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileFXService;
import org.flintcore.excel_expenses.services.receipts.ReceiptFileScheduledFXService;
import org.flintcore.utilities.animations.ViewAnimationUtils;
import org.flintcore.utilities.dates.DateUtils;
import org.flintcore.utilities.lists.ObservableListUtils;
import org.flintcore.utilities.susbcriptions.SubscriptionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Component
@Scope("prototype")
@Log4j2
public class ExpenseCreateFormController implements Initializable {

    private final ApplicationRouter appRouter;
    private final SubscriptionHolder subscriptionManager;
    private final ReceiptFileScheduledFXService receiptFileService;
    private final LocalBusinessFileFXService localBusinessFileService;

    private final ReceiptBuilderService receiptBuilderService;

    private ResourceBundle bundles;
    // Listeners
    private RNCFilterManager rncFilterManager;
    private NFCAutoCompleteListener NFCListener;

    public boolean hasRequestLocalBusiness;
    private Alert registerReceiptAlert;

    public ExpenseCreateFormController(
            ApplicationRouter appRouter,
            SubscriptionHolder subscriptionManager,
            ReceiptFileScheduledFXService receiptFileService,
            LocalBusinessFileFXService localBusinessService,
            ReceiptBuilderService receiptBuilderService
    ) {
        this.appRouter = appRouter;
        this.subscriptionManager = subscriptionManager;
        this.receiptFileService = receiptFileService;
        this.localBusinessFileService = localBusinessService;
        this.receiptBuilderService = receiptBuilderService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundles = resources;

        configureBtnRouting();
        initFXMLListeners();

        // Fields
        // Local business
        configureLocalBusinessFields();
        configureLocalBusinessRequestButton();

        // Price
        configurePriceFields();

        // Receipts
        configureSections();

        configureOnBuildRequestService();
        configureOnRegisterBtn();
    }

    private void initFXMLListeners() {
        // Add filtering logic
        rncFilterManager = new RNCFilterManager(this.localRNCTxt, this.localFilterBox);
        this.rncFilterManager.setup();
    }

    private void callDataFromServices() {
        if (this.hasRequestLocalBusiness) return;

        try {
            CompletableFuture<ObservableList<LocalBusiness>> listBusinessFuture =
                    this.localBusinessFileService.getDataList();

            listBusinessFuture.thenAcceptAsync(localBusinessList -> {
                Subscription subscription = ObservableListUtils.listenList(
                        localBusinessList, this.rncFilterManager.getItems()
                );
                this.subscriptionManager.appendSubscriptionOn(this, subscription);
            }, Platform::runLater);
        } finally {
            this.hasRequestLocalBusiness = true;
        }
    }

    private void configureSections() {
        configureReceiptSection();
        configurePriceSection();
    }

    private void configureReceiptSection() {
        configureReceiptFields();

        BooleanBinding onLocalSectionCompleted = this.rncFilterManager.selectedBusinessProperty
                .isNotNull();

        receiptSection.visibleProperty().bind(
                // Bind it while the animation move the section
                receiptSection.opacityProperty().isNotEqualTo(0.0, 0)
        );

        final double xTranslatePos = this.receiptSection.getTranslateX(),
                yTranslatePos = this.receiptSection.getTranslateY();

        Subscription localReceiptSectionSubscription = ViewAnimationUtils.animateTranslateFadedBy(
                onLocalSectionCompleted,
                Duration.millis(600),
                this.receiptSection,
                Pair.of(xTranslatePos, 0.0),
                Pair.of(yTranslatePos, 0.0),
                Pair.of(0.0, 1.0)
        );

        this.subscriptionManager.appendSubscriptionOn(this, localReceiptSectionSubscription);
    }

    private void configureReceiptFields() {
        // Receipts
        this.NFCListener = new NFCAutoCompleteListener(this.receiptNFCTxt);

        this.NFCListener.NFCProperty.subscribe(
                receiptBuilderService.getReceiptBuilder()::NFC
        );

        // Picker
        this.receiptDate.setDayCellFactory(new ByMonthCellFactory());

        this.receiptDate.valueProperty().subscribe(receiptDate ->
                receiptBuilderService.getReceiptBuilder().dateCreation(
                        DateUtils.convertToDate(receiptDate)
                )
        );
    }

    private void configureLocalBusinessFields() {
        localNameTxt.textProperty().bind(
                this.rncFilterManager.selectedBusinessProperty
                        .map(IBusiness::getName).orElse("")
        );

        // Set builder values.

        // Listen on business selection
        this.rncFilterManager.selectedBusinessProperty.subscribe(
                this.receiptBuilderService.getReceiptBuilder()::business
        );

        // Disable the RNC field if service is requesting data.
        this.localRNCTxt.disableProperty().bind(
                this.localBusinessFileService.requestingProperty()
        );
    }

    private void configureLocalBusinessRequestButton() {
        // Stop animation when done
        final Duration loadingDefaultDuration = Duration.millis(600);

        FadeTransition loadingAnimation = new FadeTransition(
                loadingDefaultDuration, this.btnLoadRNC
        );

        loadingAnimation.setAutoReverse(true);

        PauseTransition onPauseTransition = new PauseTransition(loadingDefaultDuration);

        this.localBusinessFileService.requestingProperty().subscribe((old_, requesting) -> {
            onPauseTransition.stop();

            onPauseTransition.setOnFinished(e -> {
                loadingAnimation.stop();

                loadingAnimation.setFromValue(
                        requesting ? loadingAnimation.getNode().getOpacity() : 0
                );

                loadingAnimation.setToValue(requesting ? 0 : 1.0);
                loadingAnimation.setCycleCount(requesting ? Transition.INDEFINITE : 1);

                loadingAnimation.playFromStart();
            });

            onPauseTransition.setDuration(
                    requesting ? Duration.ZERO : loadingDefaultDuration
            );

            onPauseTransition.playFromStart();
        });

        this.btnLoadRNC.setOnAction(e -> callDataFromServices());
    }

    private void configurePriceSection() {
        //Checks that fields date and NFC are not null
        BooleanBinding onReceiptSectionCompleted = this.NFCListener.NFCProperty.isNotEmpty()
                .and(this.receiptDate.valueProperty().isNotNull());

        priceSection.visibleProperty().bind(
                // Bind it while the animation move the section
                priceSection.opacityProperty().isNotEqualTo(0.0, 0)
        );

        final double xTranslatePos = this.priceSection.getTranslateX(),
                yTranslatePos = this.priceSection.getTranslateY();

        Subscription priceSectionSubscription = ViewAnimationUtils.animateTranslateFadedBy(
                onReceiptSectionCompleted,
                Duration.millis(600),
                this.priceSection,
                Pair.of(xTranslatePos, 0.0),
                Pair.of(yTranslatePos, 0.0),
                Pair.of(0.0, 1.0)
        );

        this.subscriptionManager.appendSubscriptionOn(this, priceSectionSubscription);
    }

    private void configurePriceFields() {
        // Text formatter
        List.of(this.priceTxt, this.priceItbisTxt, this.percentServicePriceTxt)
                .forEach(tx -> tx.setTextFormatter(new StaticNumericFormatter(2)));

        this.priceTxt.textProperty().subscribe(
                SubscriptionUtils.consumeBiSubscribeMap(
                        p -> receiptBuilderService.getReceiptBuilder().price(p)
                )
        );

        this.percentServicePriceTxt.textProperty().subscribe(
                SubscriptionUtils.consumeBiSubscribeMap(
                        p -> receiptBuilderService.getReceiptBuilder().servicePrice(p)
                )
        );

        this.priceItbisTxt.textProperty().subscribe(
                SubscriptionUtils.consumeBiSubscribeMap(
                        p -> receiptBuilderService.getReceiptBuilder().itbPrice(p)
                )
        );
    }

    private void configureOnBuildRequestService() {
        final PauseTransition closeAlertWait = new PauseTransition(Duration.seconds(4));

        // Hide and set to null the given alert.
        closeAlertWait.setOnFinished(e -> NullableUtils.executeNonNull(this.registerReceiptAlert, () -> {
            this.registerReceiptAlert.close();
            this.registerReceiptAlert = null;
        }));

        // Start build receipt
        this.receiptBuilderService.addSubscription(TaskFxEvent.WORKER_STATE_SCHEDULED, () -> {
            NullableUtils.executeNonNull(this.registerReceiptAlert, alert -> {
                alert.setHeaderText(
                        this.bundles.getString("receipts.message.building")
                );
                alert.show();
            });
        });

        // Fail build receipt
        this.receiptBuilderService.addSubscription(TaskFxEvent.WORKER_STATE_FAILED, () -> {
            NullableUtils.executeNonNull(this.registerReceiptAlert, alert -> alert.setHeaderText(
                    this.bundles.getString("receipts.message.build-fail")
            ));

            closeAlertWait.playFromStart();
        });

        // Build receipt successfully listener.
        this.receiptBuilderService.addSubscription(TaskFxEvent.WORKER_STATE_SUCCEEDED, () -> {
            Receipt receiptBuilt = this.receiptBuilderService.getValue();

            this.receiptFileService.register(receiptBuilt).thenAcceptAsync(saved -> {
                String message = this.bundles.getString(saved ?
                        "receipts.message.register-success" : "receipts.message.register-duplicated"
                );

                NullableUtils.executeNonNull(this.registerReceiptAlert, alert -> alert.setHeaderText(message));

                if (saved) {
                    this.receiptBuilderService.clearData();
                    clearFormData();
                }

                closeAlertWait.playFromStart();
            }, Platform::runLater).exceptionallyAsync(th -> {
                NullableUtils.executeNonNull(this.registerReceiptAlert, alert -> alert.setHeaderText(
                        this.bundles.getString("receipts.message.register-fail")
                ));

                return null;
            }, Platform::runLater);
        });

        this.receiptBuilderService.addSubscription(TaskFxEvent.WORKER_STATE_FAILED, () -> {
            NullableUtils.executeNonNull(this.registerReceiptAlert, alert -> alert.setHeaderText(
                    this.bundles.getString("receipts.message.build-fail")
            ));
        });
    }

    private void configureOnRegisterBtn() {
        this.btnSubmit.setOnAction(e -> {
            if (this.receiptBuilderService.isRunning() || Objects.nonNull(this.registerReceiptAlert)) return;

            this.registerReceiptAlert = new Alert(Alert.AlertType.INFORMATION);
            this.registerReceiptAlert.setTitle(
                    this.bundles.getString("receipts.message.register")
            );
            // Request on build data provided.
            this.receiptBuilderService.restart();
        });
    }

    private void configureBtnRouting() {
        this.btnBack.setOnMousePressed(evt -> this.appRouter.navigateBack());
        this.btnRegisterLocal.setOnAction(e -> this.appRouter.navigateTo(ELocalRoute.CREATE));
    }

    /**
     * Clean all fields on the view.
     */
    private void clearFormData() {
        // Locals
        this.localRNCTxt.clear();

        // Receipts
        this.receiptDate.setValue(null);
        this.receiptNFCTxt.clear();

        // Price
        this.priceTxt.clear();
        this.percentServicePriceTxt.clear();
        this.priceItbisTxt.clear();
    }

    @PreDestroy
    private void onDeleteReference() {
        log.info("Cleaning subscriptions from {} ...", getClass().getSimpleName());
        this.rncFilterManager.close();
        this.subscriptionManager.close();
    }

    @FXML
    private StackPane btnBack;

    @FXML
    private Button btnLoadRNC;

    @FXML
    private Button btnRegisterLocal;

    @FXML
    private Button btnSubmit;

    @FXML
    private ComboBox<IBusiness> localFilterBox;

    @FXML
    private TextField localNameTxt;

    @FXML
    private TextField localRNCTxt;

    @FXML
    private VBox localSection;

    @FXML
    private TextField percentServicePriceTxt;

    @FXML
    private HBox priceExtraHolder;

    @FXML
    private TextField priceItbisTxt;

    @FXML
    private VBox priceSection;

    @FXML
    private TextField priceTxt;

    @FXML
    private DatePicker receiptDate;

    @FXML
    private TextField receiptNFCTxt;

    @FXML
    private VBox receiptSection;
}
