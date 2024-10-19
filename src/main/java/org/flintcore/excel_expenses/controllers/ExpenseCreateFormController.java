package org.flintcore.excel_expenses.controllers;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.handlers.fields.NumericTextFieldChangeListener;
import org.flintcore.excel_expenses.handlers.fields.TextFieldChangeListener;
import org.flintcore.excel_expenses.handlers.fields.dates.LocalDateChangeListener;
import org.flintcore.excel_expenses.handlers.filters.BasicBusinessStringConverter;
import org.flintcore.excel_expenses.managers.events.combo_box.keyboards.ComboBoxRemoteKeyListener;
import org.flintcore.excel_expenses.managers.events.texts.fitlters.RNCFilterEventManager;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.managers.routers.local.ELocalRoute;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.expenses.Receipt;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileFXService;
import org.flintcore.utilities.dates.DateUtils;
import org.flintcore.utilities.lists.ObservableListUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
@Scope("prototype")
@Log4j2
public class ExpenseCreateFormController implements Initializable {

    private final ApplicationRouter appRouter;
    private final SubscriptionHolder subscriptionManager;
    private final LocalBusinessFileFXService localBusinessService;
    private Receipt.ReceiptBuilder receiptBuilder;
    private LocalBusiness.LocalBusinessBuilder businessBuilder;
    private ResourceBundle bundles;

    // Listeners
    private RNCFilterEventManager rncFilterEventManager;

    public ExpenseCreateFormController(
            ApplicationRouter appRouter,
            SubscriptionHolder subscriptionManager,
            LocalBusinessFileFXService localBusinessService
    ) {
        this.appRouter = appRouter;
        this.subscriptionManager = subscriptionManager;
        this.localBusinessService = localBusinessService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundles = resources;

        configureBtnRouting();
        initFXMLListeners();

        // Fields
        // Local business
        configureLocalBusinessFields();
        configureLocalBusinessFilterBox();
        configureLocalBusinessRequestButton();

        // Price
        configurePriceFields();


        configureReceiptFields();
        configureSections();

        onFirstRequestBusinessList();
    }

    private void initFXMLListeners() {
        this.rncFilterEventManager = new RNCFilterEventManager(this.localRNCTxt, this.localFilterBox);
    }

    private void onFirstRequestBusinessList() {
        AtomicReference<Subscription> onFirstRequestDataSet = new AtomicReference<>();

        PauseTransition onFilterRNC = new PauseTransition(Duration.seconds(1));

        Runnable onFirstRequest = () -> {
            NullableUtils.executeNonNull(onFirstRequestDataSet.get(), Subscription::unsubscribe);

            CompletableFuture<ObservableList<LocalBusiness>> listBusinessFuture = this.localBusinessService
                    .getLocalBusinessList();

            listBusinessFuture.thenAcceptAsync(localBusinessList -> {
                ObservableList<IBusiness> boxItems = this.localFilterBox.getItems(),
                        concatenatedList = ObservableListUtils.concat(
                                boxItems, localBusinessList
                        );

                FilteredList<IBusiness> businessList = new FilteredList<>(concatenatedList);

                this.localFilterBox.setItems(businessList);

                businessList.addListener((ListChangeListener<? super IBusiness>) change -> {
                    System.out.printf("List changed: %s%n", change.getList());
                });
            }, Platform::runLater);
        };

        onFirstRequestDataSet.set(
                this.localBusinessService.listenRequestTask(
                        TaskFxEvent.WORKER_STATE_SCHEDULED,
                        onFirstRequest
                )
        );

        this.subscriptionManager.appendSubscriptionOn(
                this.localBusinessService,
                onFirstRequestDataSet.get()
        );
    }

    private void configureLocalBusinessFields() {
        this.localNameTxt.textProperty().subscribe(
                consumeBusinessBuilder(n -> this.businessBuilder.name(n))
        );

        this.localRNCTxt.textProperty().subscribe(
                consumeBusinessBuilder(RNC -> this.businessBuilder.RNC(RNC))
        );

        this.localRNCTxt.textProperty().subscribe(this.localFilterBox::show);
    }

    private void configureLocalBusinessFilterBox() {
        this.localFilterBox.converterProperty().set(
                new BasicBusinessStringConverter<>(this.localFilterBox::getItems)
        );

        ComboBoxRemoteKeyListener rncKeyListener = this.rncFilterEventManager
                .getRemoteRNCKeyListener();


    }

    private void configureLocalBusinessRequestButton() {
        // Stop animation when done
        AtomicReference<FadeTransition> onLoadingAnimation = new AtomicReference<>();

        Subscription onDoneRequest = this.localBusinessService.listenRequestTask(
                List.of(
                        WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                        WorkerStateEvent.WORKER_STATE_FAILED,
                        WorkerStateEvent.WORKER_STATE_CANCELLED
                ), () -> {
                    PauseTransition onPauseTransition = new PauseTransition(
                            Duration.seconds(.5)
                    );

                    onPauseTransition.setOnFinished(e -> {
                        FadeTransition loadingAnimation = onLoadingAnimation.getAndSet(null);

                        loadingAnimation.stop();
                        loadingAnimation.setFromValue(this.btnLoadRNC.getOpacity());
                        loadingAnimation.setToValue(1.0);
                        loadingAnimation.setCycleCount(1);

                        loadingAnimation.play();
                    });

                    onPauseTransition.play();
                }
        );

        this.subscriptionManager.appendSubscriptionOn(
                this.localBusinessService, onDoneRequest
        );

        this.btnLoadRNC.setOnAction(e -> {
            this.localBusinessService.requestData();
            if (this.localBusinessService.isRequestingData()) {
                FadeTransition loadingAnimation = new FadeTransition(
                        Duration.millis(600), this.btnLoadRNC
                );

                loadingAnimation.setFromValue(1.0);
                loadingAnimation.setToValue(0D);
                loadingAnimation.setCycleCount(Timeline.INDEFINITE);
                loadingAnimation.setAutoReverse(true);

                onLoadingAnimation.set(loadingAnimation);

                loadingAnimation.play();
            }
        });
    }

    private void configureReceiptFields() {
        // Receipts
        this.receiptNFCTxt.textProperty().addListener(
                new TextFieldChangeListener(
                        consumeReceiptBuilder(NFC -> this.receiptBuilder.NFC(NFC))
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
    }

    private void configurePriceFields() {
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
    }

    private void configureSections() {
        configureReceiptSection();
    }

    private void configureReceiptSection() {
        Binding<Boolean> onLocalSectionCompleted = Bindings.or(
                this.localNameTxt.textProperty().isEmpty(),
                this.localRNCTxt.textProperty().isEmpty()
        ).not();

        final double xTranslatePos = this.receiptSection.getTranslateX(),
                yTranslatePos = this.receiptSection.getTranslateY();

        AtomicReference<ParallelTransition> onReceiptTransition = new AtomicReference<>();

        onLocalSectionCompleted.subscribe(completed -> {
            NullableUtils.executeNonNull(onReceiptTransition.get(), Transition::stop);

            final TranslateTransition translateTransition = new TranslateTransition(
                    Duration.millis(600.0), this.receiptSection
            );

            final FadeTransition fadeTransition = new FadeTransition(
                    Duration.millis(600.0), this.receiptSection
            );

            final ParallelTransition showReceiptTransition = new ParallelTransition(
                    translateTransition, fadeTransition
            );

            onReceiptTransition.set(showReceiptTransition);

            translateTransition.setFromX(completed ? xTranslatePos : 0);
            translateTransition.setFromY(completed ? yTranslatePos : 0);
            translateTransition.setToX(completed ? 0 : xTranslatePos);
            translateTransition.setToY(completed ? 0 : yTranslatePos);

            fadeTransition.setFromValue(completed ? 0 : 1.0);
            fadeTransition.setToValue(completed ? 1.0 : 0);

            showReceiptTransition.setOnFinished(e -> onReceiptTransition.set(null));

            showReceiptTransition.play();
        });
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
        return value -> {
            NullableUtils.executeIsNull(this.businessBuilder,
                    () -> this.businessBuilder = LocalBusiness.builder()
            );

            consumer.accept(value);
        };
    }

    private void configureBtnRouting() {
        this.btnBack.setOnMousePressed(evt -> this.appRouter.navigateBack());
        this.btnRegisterLocal.setOnAction(e -> this.appRouter.navigateTo(ELocalRoute.CREATE));
    }

    @PreDestroy
    private void onDeleteReference() {
        String message = "Cleaning subscriptions from %s ...".formatted(getClass().getSimpleName());
        log.info(message);
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
