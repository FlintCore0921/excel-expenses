package org.flintcore.excel_expenses.excels_handler.services.business;

import data.utils.NullableUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.managers.subscriptions.tasks.ObservableFXService;
import org.flintcore.excel_expenses.excels_handler.managers.tasks.business.locals.BuildLocalBusinessTask;
import org.flintcore.excel_expenses.excels_handler.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.excels_handler.models.expenses.LocalBusiness;
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
public class RegisterLocalBusinessFXService extends ObservableFXService<Boolean> {
    private static final String BUSINESS_REGISTERED_SUCCESSFULLY = "Local business registered successfully.";
    private static final String BUSINESS_ALREADY_EXISTS = "The business already exists.";

    private final LocalBusinessValidator localBusinessValidator;
    private final LocalBusinessFileFXService localBusinessFXService;

    @Setter
    private Supplier<String> localNameSupplier;
    @Setter
    private Supplier<String> localRNCSupplier;
    @Setter
    private Consumer<Exception> errorConsumer;

    public RegisterLocalBusinessFXService(
            LocalBusinessFileFXService localBusinessFXService,
            LocalBusinessValidator localBusinessValidator
    ) {
        super();
        this.localBusinessValidator = localBusinessValidator;
        this.localBusinessFXService = localBusinessFXService;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                boolean result = false;

                try {
                    updateMessage("Building Local business...");
                    LocalBusiness localBusiness = new BuildLocalBusinessTask(
                            localBusinessValidator,
                            localNameSupplier,
                            localRNCSupplier
                    ).get();

                    CompletableFuture<Boolean> registrationResponse = localBusinessFXService
                            .register(localBusiness);
                    updateMessage("Registering business...");

                    result = registrationResponse.get();

                    if (registrationResponse.isCompletedExceptionally()) {
                        throw new RuntimeException("Unable to store data.");
                    }
                } catch (Exception e) {
                    consumeException(e);
                    updateMessage("Fail at build product!");
                } finally {
                    String finallyMessage = result ? BUSINESS_REGISTERED_SUCCESSFULLY
                            : BUSINESS_ALREADY_EXISTS;

                    updateMessage(finallyMessage);
                }

                return result;
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
