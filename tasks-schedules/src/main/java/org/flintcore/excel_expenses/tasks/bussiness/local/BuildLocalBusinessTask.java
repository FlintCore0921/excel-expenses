package org.flintcore.excel_expenses.tasks.bussiness.local;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Log4j2
public class BuildLocalBusinessTask implements Supplier<LocalBusiness> {
    private static final String BUILDING_RESULT = "Building result...";
    private static final String VALIDATING_DATA = "Validating data...";

    private final LocalBusinessValidator localBusinessValidator;

    private StringProperty messageProperty;

    private Supplier<String> localNameSupplier;
    private Supplier<String> localRNCSupplier;

    public BuildLocalBusinessTask(LocalBusinessValidator localBusinessValidator) {
        this.localBusinessValidator = localBusinessValidator;
    }

    public BuildLocalBusinessTask() {
        this(new LocalBusinessValidator());
    }

    @Override
    public LocalBusiness get()  {
        LocalBusiness localBusiness;
        try {
            String localName = localNameSupplier.get(),
                    localRNC = localRNCSupplier.get();

            updateMessage(VALIDATING_DATA);
            validateProvidedValues(localName, localRNC);

            updateMessage(BUILDING_RESULT);
            localBusiness = LocalBusiness.builder()
                    .name(localName)
                    .RNC(localRNC)
                    .build();

            updateMessage("Last steps...");
            validateBusinessContent(localBusiness);
        } catch (Exception e) {
            updateMessage("Fail at build product!");
            throw e;
        }

        return localBusiness;
    }

    private void validateProvidedValues(String localName, String localRNC) {
        if (Stream.of(localName, localRNC).anyMatch(String::isBlank)) {
            log.error("Error at moment to check fields for local business.");
            throw new NullPointerException("Fields required for local business.");
        }
    }

    private void validateBusinessContent(LocalBusiness localBusiness) {
        if (!localBusinessValidator.validateContent(localBusiness)) {
            log.error("Error at moment to validate data for building local business.");
            throw new IllegalArgumentException("Invalid format of the data from Local business.");
        }
    }

    public void updateMessage(String message) {
        if(Platform.isFxApplicationThread()) {
            messageProperty.set(message);
        } else {
            Platform.runLater(() -> messageProperty.set(message));
        }
    }
}
