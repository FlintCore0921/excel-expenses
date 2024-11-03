package org.flintcore.excel_expenses.managers.events.texts.fillers;

import data.utils.NullableUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.NonNull;
import org.apache.commons.lang3.ObjectUtils;
import org.flintcore.excel_expenses.managers.factories.receipts.LocalNFCFactory;
import org.flintcore.excel_expenses.managers.rules.IReceiptRules;
import org.flintcore.excel_expenses.models.properties.formatters.NFCFormatter;
import org.flintcore.excel_expenses.managers.subscriptions.consumers.ISubscriptionConsumerHandler;
import org.flintcore.excel_expenses.managers.subscriptions.consumers.MultiSubscriptionConsumerHandler;

public class NFCAutoCompleteListener {
    protected final TextField nfcFilter;
    protected LocalNFCFactory localNfcFactory;
    protected ISubscriptionConsumerHandler<KeyCode, Runnable> keyHandler;

    public final StringProperty NFCProperty;

    public NFCAutoCompleteListener(@NonNull TextField nfcFilter) {
        this.nfcFilter = nfcFilter;
        NFCProperty = new SimpleStringProperty();
        setup();
    }

    protected void setup() {
        initHandlerListener();
        setTextListener();
    }

    protected void setTextListener() {
        this.nfcFilter.setOnKeyPressed(e -> this.keyHandler.accept(e.getCode()));

        this.nfcFilter.setTextFormatter(
                new NFCFormatter(IReceiptRules::getCodeFormatterPattern)
        );

        this.nfcFilter.focusedProperty().subscribe((old, curr) -> callFieldValidation());

        this.keyHandler.addLastSubscription(KeyCode.ENTER, this::callFieldValidation);
    }

    protected void initHandlerListener() {
        NullableUtils.executeIsNull(this.keyHandler,
                () -> this.keyHandler = new MultiSubscriptionConsumerHandler<>()
        );
    }

    protected void callFieldValidation() {
        this.initNFCFactory();
        String NFCFiltered = this.nfcFilter.getText();

        final String result = localNfcFactory.apply(NFCFiltered),
                NFC = ObjectUtils.defaultIfNull(result, "");

        this.NFCProperty.set(NFC);
        this.nfcFilter.setText(NFC);
    }

    protected void initNFCFactory() {
        NullableUtils.executeIsNull(this.localNfcFactory,
                () -> this.localNfcFactory = new LocalNFCFactory()
        );
    }
}
