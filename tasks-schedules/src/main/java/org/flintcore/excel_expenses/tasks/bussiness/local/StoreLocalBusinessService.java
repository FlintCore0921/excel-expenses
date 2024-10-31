package org.flintcore.excel_expenses.tasks.bussiness.local;

import data.utils.NullableUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.managers.subscriptions.tasks.ObservableFXService;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileScheduledFXService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Log4j2
@Component
@Scope("prototype")
public class StoreLocalBusinessService extends ObservableFXService<Boolean> {
    private final LocalBusinessValidator localBusinessValidator;
    private final LocalBusinessFileScheduledFXService localBusinessFXService;

    @Setter
    private Supplier<String> localNameSupplier;
    @Setter
    private Supplier<String> localRNCSupplier;
    @Setter
    private Consumer<Exception> errorConsumer;

    public StoreLocalBusinessService(
            LocalBusinessFileScheduledFXService localBusinessFXService,
            LocalBusinessValidator localBusinessValidator) {
        super();
        this.localBusinessValidator = localBusinessValidator;
        this.localBusinessFXService = localBusinessFXService;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            public static final String BUILDING_RESULT = "Building result...";
            public static final String VALIDATING_DATA = "Validating data...";

            @Override
            protected Boolean call() throws Exception {
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

                    CompletableFuture<Boolean> registrationResponse = localBusinessFXService
                            .register(localBusiness);

                    if (registrationResponse.isCompletedExceptionally() || !registrationResponse.get()) {
                        throw new RuntimeException("Unable to store data.");
                    }

                    updateMessage("Local business created successfully.");
                    return true;

                } catch (Exception e) {
                    consumeException(e);
                    updateMessage("Fail at build product!");
                }

                return false;
            }
        };
    }

    @Override
    protected void succeeded() {
        super.succeeded();
    }

    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.getEventListenerHolder().get(type).remove(action);
        });
    }

    @Override
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
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

    private void consumeException(Exception e) {
        NullableUtils.executeNonNull(this.errorConsumer, c -> c.accept(e));
    }
}
