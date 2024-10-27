package org.flintcore.excel_expenses.tasks.bussiness.local;

import data.utils.NullableUtils;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.Setter;
import org.flintcore.excel_expenses.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableTask;
import org.flintcore.excel_expenses.services.business.LocalBusinessFileFXService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RegisterLocalBusinessOnFileTask extends ObservableTask<Void> {
    private final LocalBusinessFileFXService localBusinessFileService;
    private final LocalBusinessValidator localBusinessValidator;
    private final Supplier<String> localNameSupplier;
    private final Supplier<String> localRNCSupplier;
    @Setter
    private Consumer<Exception> errorConsumer;

    @Setter
    private Runnable onDone;

    public RegisterLocalBusinessOnFileTask(
            LocalBusinessFileFXService localBusinessFileService,
            LocalBusinessValidator localBusinessValidator,
            Supplier<String> localNameSupplier,
            Supplier<String> localRNCSupplier
    ) {
        super();
        this.localBusinessFileService = localBusinessFileService;
        this.localBusinessValidator = localBusinessValidator;
        this.localNameSupplier = localNameSupplier;
        this.localRNCSupplier = localRNCSupplier;
    }

    @Override
    protected Void call() throws Exception {
        try {
            String localName = this.localNameSupplier.get(),
                    localRNC = this.localRNCSupplier.get();

            validateProvidedValues(localName, localRNC);

            LocalBusiness localBusiness = LocalBusiness.builder()
                    .name(localName)
                    .RNC(localRNC)
                    .build();

            validateBusinessContent(localBusiness);

            Future<Boolean> wasBusinessAdded = this.localBusinessFileService
                    .register(localBusiness);

            if (!wasBusinessAdded.get()) {
                throw new RuntimeException("Business already exists");
            }
        } catch (Exception e) {
            this.consumeException(e);
            throw e;
        }

        return null;
    }

    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.events.get(type).remove(action);
        });
    }

    @Override
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }

    @Override
    protected void done() {
        NullableUtils.executeNonNull(this.onDone);
    }

    private void validateProvidedValues(String localName, String localRNC) {
        if (Stream.of(localName, localRNC).anyMatch(String::isBlank)) {
            getLogger().error("Error at moment to check fields.");
            throw new NullPointerException("Fields required.");
        }
    }

    private void validateBusinessContent(LocalBusiness localBusiness) {
        if (!localBusinessValidator.validateContent(localBusiness)) {
            getLogger().error("Error at moment to validate data for building.");
            throw new IllegalArgumentException("Invalid format of the data from Local business");
        }
    }

    private void consumeException(Exception e) {
        NullableUtils.executeNonNull(this.errorConsumer, c -> c.accept(e));
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
