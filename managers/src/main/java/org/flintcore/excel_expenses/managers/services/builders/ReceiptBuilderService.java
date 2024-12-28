package org.flintcore.excel_expenses.managers.services.builders;

import data.utils.NullableUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import org.flintcore.excel_expenses.managers.services.FxService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IOnceEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.validators.receipts.ReceiptValidator;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service for local receipt based to capture {@link LocalBusiness} and {@link Receipt} implementations.
 */
@Component
@Scope("prototype")
public final class ReceiptBuilderService extends FxService<Receipt>
        implements IEventSubscriptionHandler<WorkerStateEvent, Runnable>,
        IOnceEventSubscriptionHandler<WorkerStateEvent, Runnable> {
    private Receipt.ReceiptBuilder receiptBuilder;
    // Validators
    private final ReceiptValidator receiptValidator;

    public ReceiptBuilderService(
            ShutdownFXApplication shutdownFXApplication,
            GeneralEventSubscriptionHandler eventHandler,
            ReceiptValidator receiptValidator) {
        super(eventHandler, shutdownFXApplication);
        this.receiptValidator = receiptValidator;
    }

    public Receipt.ReceiptBuilder getReceiptBuilder() {
        NullableUtils.executeIsNull(this.receiptBuilder,
                () -> this.receiptBuilder = Receipt.builder());

        return this.receiptBuilder;
    }

    @Override
    protected Task<Receipt> createTask() {
        return new Task<>() {
            private final ReceiptBuilderService OWNER = ReceiptBuilderService.this;

            @Override
            protected Receipt call() throws IllegalArgumentException {
                // Get receipt builder
                Receipt.ReceiptBuilder ownerReceiptBuilder = OWNER.getReceiptBuilder();

                // Receipt built
                Receipt receipt = ownerReceiptBuilder.build();

                if (!OWNER.receiptValidator.validate(receipt)) {
                    throw new IllegalArgumentException();
                }

                return receipt;
            }
        };
    }

    public void clearData() {
        this.receiptBuilder = null;
    }
}
