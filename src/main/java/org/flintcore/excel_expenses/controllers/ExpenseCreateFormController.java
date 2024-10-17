package org.flintcore.excel_expenses.controllers;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
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
import org.flintcore.excel_expenses.handlers.fields.NumericTextFieldChangeListener;
import org.flintcore.excel_expenses.handlers.fields.TextFieldChangeListener;
import org.flintcore.excel_expenses.handlers.fields.businesses.BusinessSelectionListener;
import org.flintcore.excel_expenses.handlers.fields.dates.LocalDateChangeListener;
import org.flintcore.excel_expenses.handlers.filters.BusinessStringConverter;
import org.flintcore.excel_expenses.handlers.focus.TextFieldFocusListener;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.listeners.keyboards.ComboBoxRemoteKeyListener;
import org.flintcore.excel_expenses.managers.routers.local.ELocalRoute;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.expenses.Receipt;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileFXService;
import org.flintcore.utilities.dates.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class ExpenseCreateFormController implements Initializable {

    private final ApplicationRouter appRouter;
    private final SubscriptionHolder subscriptionManager;
    private final LocalBusinessFileFXService localBusinessService;
    private Receipt.ReceiptBuilder receiptBuilder;
    private LocalBusiness.LocalBusinessBuilder businessBuilder;

    public ExpenseCreateFormController(ApplicationRouter appRouter, SubscriptionHolder subscriptionManager, LocalBusinessFileFXService localBusinessService) {
        this.appRouter = appRouter;
        this.subscriptionManager = subscriptionManager;
        this.localBusinessService = localBusinessService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureBtnRouting();

        // Fields
        // Local business
        configureLocalBusinessFields();

        // Filter Local
        configureBusinessFilterBox();

        // Receipts
        this.receiptNFCTxt.textProperty().addListener(
                new TextFieldChangeListener(
                        consumeReceiptBuilder(NFC -> this.receiptBuilder.NFC(NFC))
                )
        );

        // Price
        this.priceTxt.textProperty().addListener(
                new NumericTextFieldChangeListener<>(
                        consumeReceiptBuilder(price -> this.receiptBuilder.price(price)),
                        Double::parseDouble
                )
        );

        this.percentServicePriceTxt.textProperty().addListener(
                new NumericTextFieldChangeListener<>(
                        consumeReceiptBuilder(servPrc -> this.receiptBuilder.servicePrice(servPrc)),
                        Double::parseDouble
                )
        );

        this.priceItbisTxt.textProperty().addListener(
                new NumericTextFieldChangeListener<>(
                        consumeReceiptBuilder(itb -> this.receiptBuilder.itbPrice(itb)),
                        Double::parseDouble
                )
        );

        this.receiptDate.valueProperty().addListener(
                new LocalDateChangeListener(
                        consumeReceiptBuilder(
                                dateCreation -> this.receiptBuilder.dateCreation(
                                        DateUtils.convertToDate(dateCreation)
                                ))
                )
        );

        // Button fields
        configureLocalRequestButton();

        // Button add Expense Local
    }

    private void configureBtnRouting() {
        this.btnBack.setOnMousePressed(evt -> this.appRouter.navigateBack());
        this.btnRegisterLocal.setOnAction(e -> this.appRouter.navigateTo(ELocalRoute.CREATE));
    }

    // Set up the load again btn
    private void configureLocalRequestButton() {
        ObservableList<IBusiness> boxItems = this.localFilterBox.getItems();

        Timeline loadingAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(this.btnLoadRNC.opacityProperty(), 1.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(this.btnLoadRNC.opacityProperty(), 0.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(this.btnLoadRNC.opacityProperty(), 1.0, Interpolator.EASE_BOTH))
        );

        loadingAnimation.setCycleCount(Timeline.INDEFINITE);

        // Stop animation when done
         Subscription onDoneRequest = this.localBusinessService.listenRequestTask(
                List.of(
                        WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                        WorkerStateEvent.WORKER_STATE_FAILED,
                        WorkerStateEvent.WORKER_STATE_CANCELLED
                ), loadingAnimation::stop
        );

        this.subscriptionManager.appendSubscriptionOn(
                this.localBusinessService, onDoneRequest
        );

        this.btnLoadRNC.setOnAction(e -> {
            boxItems.clear();
            loadingAnimation.playFromStart();
            localBusinessService.requestData();
        });
    }

    private void configureLocalBusinessFields() {
        this.localNameTxt.textProperty().addListener(
                new TextFieldChangeListener(
                        consumeBusinessBuilder(n -> this.businessBuilder.name(n))
                )
        );

        // Focus shows list of items
        this.localRNCTxt.focusedProperty().addListener(
                new TextFieldFocusListener(
                        this.localFilterBox::show
                )
        );

        // UP and DOWN movement
        this.localRNCTxt.setOnKeyPressed(
                new ComboBoxRemoteKeyListener<>(this.localFilterBox)
        );

        this.localRNCTxt.textProperty().addListener(
                new TextFieldChangeListener(
                        consumeBusinessBuilder(RNC -> this.businessBuilder.RNC(RNC))
                )
        );
    }

    private void configureBusinessFilterBox() {
        this.localFilterBox.converterProperty().set(
                new BusinessStringConverter(this.localFilterBox::getItems)
        );
        // Set the value of current combo box
        this.localFilterBox.valueProperty().subscribe(
                new BusinessSelectionListener(
                        this.localNameTxt, this.localRNCTxt
                )
        );
    }

    private <T> Consumer<T> consumeReceiptBuilder(Consumer<T> consumer) {
        return r -> {
            NullableUtils.executeIsNull(this.receiptBuilder,
                    () -> this.receiptBuilder = Receipt.builder()
            );

            consumer.accept(r);
        };
    }

    private <T> Consumer<T> consumeBusinessBuilder(Consumer<T> consumer) {
        return b -> {
            NullableUtils.executeIsNull(this.businessBuilder,
                    () -> this.businessBuilder = LocalBusiness.builder()
            );

            consumer.accept(b);
        };
    }

    @PreDestroy
    private void onDeleteReference() {
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
