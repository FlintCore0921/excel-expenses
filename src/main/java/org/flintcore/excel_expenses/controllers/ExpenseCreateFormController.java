package org.flintcore.excel_expenses.controllers;

import jakarta.annotation.PreDestroy;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
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
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.properties.formatters.StaticNumericFormatter;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileScheduledFXService;
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
    private final LocalBusinessFileScheduledFXService localBusinessService;

    // Required builders
    private final ReceiptBuilderService receiptBuilderService;

    @SuppressWarnings("all")
    private ResourceBundle _bundles;
    // Listeners
    private RNCFilterManager rncFilterManager;
    private NFCAutoCompleteListener NFCListener;

    public boolean hasRequestLocalBusiness;

    public ExpenseCreateFormController(
            ApplicationRouter appRouter,
            SubscriptionHolder subscriptionManager,
            LocalBusinessFileScheduledFXService localBusinessService,
            ReceiptBuilderService receiptBuilderService
    ) {
        this.appRouter = appRouter;
        this.subscriptionManager = subscriptionManager;
        this.localBusinessService = localBusinessService;
        this.receiptBuilderService = receiptBuilderService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this._bundles = resources;

        configureBtnRouting();
        initFXMLListeners();

        // Fields
        // Local business
        configureLocalBusinessFields();
        configureLocalBusinessRequestButton();

        // Price
        configurePriceFields();

        // Receipts
        configureReceiptFields();
        configureSections();

        configureLocalService();
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
                    this.localBusinessService.getDataList();

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

    private void configureLocalBusinessFields() {
        localNameTxt.textProperty().bind(
                this.rncFilterManager.selectedBusinessProperty
                        .map(IBusiness::getName).orElse("")
        );

        this.rncFilterManager.selectedBusinessProperty.subscribe((old, business) -> {
            if (Objects.isNull(business)) return;
            this.localRNCTxt.setText(business.getRNC());
        });

        this.localNameTxt.textProperty().subscribe(
                receiptBuilderService.getBusinessBuilder()::name
        );

        this.localRNCTxt.textProperty().subscribe(
                receiptBuilderService.getBusinessBuilder()::RNC
        );

        // Disable the RNC field if service is requesting data.
        this.localRNCTxt.disableProperty().bind(
                this.localBusinessService.requestingProperty()
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

        this.localBusinessService.requestingProperty().subscribe((old_, requesting) -> {
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

    private void configureSections() {
        configureReceiptSection();
        configurePriceSection();
    }

    private void configureReceiptSection() {
        BooleanBinding onLocalSectionCompleted = this.rncFilterManager.selectedBusinessProperty
                .isNotNull();

        receiptSection.visibleProperty().bind(
                // Bind it while the animation move the section
                receiptSection.opacityProperty().isNotEqualTo(0.0, 0)
        );

        final double xTranslatePos = this.receiptSection.getTranslateX(),
                yTranslatePos = this.receiptSection.getTranslateY();

        Subscription localReceiptSectionSubscription = ViewAnimationUtils.animateTranslateFadedBySubscription(
                onLocalSectionCompleted,
                Duration.millis(600),
                this.receiptSection,
                Pair.of(xTranslatePos, 0.0),
                Pair.of(yTranslatePos, 0.0),
                Pair.of(0.0, 1.0)
        );

        this.subscriptionManager.appendSubscriptionOn(this, localReceiptSectionSubscription);
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

        Subscription priceSectionSubscription = ViewAnimationUtils.animateTranslateFadedBySubscription(
                onReceiptSectionCompleted,
                Duration.millis(600),
                this.priceSection,
                Pair.of(xTranslatePos, 0.0),
                Pair.of(yTranslatePos, 0.0),
                Pair.of(0.0, 1.0)
        );

        this.subscriptionManager.appendSubscriptionOn(this, priceSectionSubscription);
    }


    private void configureLocalService() {
        this.receiptBuilderService.setOnSucceeded(e -> {
            Receipt receiptBuilt = this.receiptBuilderService.getValue();

            // TODO ADD TO RECEIPT SERVICE
        });
    }

    private void configureBtnRouting() {
        this.btnBack.setOnMousePressed(evt -> this.appRouter.navigateBack());
        this.btnRegisterLocal.setOnAction(e -> this.appRouter.navigateTo(ELocalRoute.CREATE));
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
