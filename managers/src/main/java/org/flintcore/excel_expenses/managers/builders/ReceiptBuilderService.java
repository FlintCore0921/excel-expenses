package org.flintcore.excel_expenses.managers.builders;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.managers.validators.receipts.ReceiptValidator;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public final class ReceiptBuilderService extends Service<Receipt> {
    @Getter(lazy = true)
    private final Receipt.ReceiptBuilder receiptBuilder = Receipt.builder();
    @Getter(lazy = true)
    private final LocalBusiness.LocalBusinessBuilder businessBuilder = LocalBusiness.builder();


    private final LocalBusinessValidator localBusinessValidator;

    // Validators
    private final ReceiptValidator receiptValidator;

    public ReceiptBuilderService(
            LocalBusinessValidator localBusinessValidator,
            ReceiptValidator receiptValidator
    ) {
        this.localBusinessValidator = localBusinessValidator;
        this.receiptValidator = receiptValidator;
    }

    @Override
    protected Task<Receipt> createTask() {
        return new Task<>() {
            private final ReceiptBuilderService OWNER = ReceiptBuilderService.this;

            @Override
            protected Receipt call() throws IllegalArgumentException {
                // Local business builder
                LocalBusiness.LocalBusinessBuilder ownerBusinessBuilder = OWNER.getBusinessBuilder();

                // Local business built
                LocalBusiness localBusiness = ownerBusinessBuilder.build();

                // local business validator

                if (!OWNER.localBusinessValidator.validateContent(localBusiness)) {
                    throw new IllegalArgumentException();
                }

                // Get receipt builder
                Receipt.ReceiptBuilder ownerReceiptBuilder = OWNER.getReceiptBuilder();

                ownerReceiptBuilder.business(localBusiness);

                // Receipt built
                Receipt receipt = OWNER.getReceiptBuilder().build();

                if (!OWNER.receiptValidator.validate(receipt)) {
                    throw new IllegalArgumentException();
                }

                return receipt;
            }
        };
    }
}
