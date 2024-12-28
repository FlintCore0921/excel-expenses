package org.flintcore.excel_expenses.configurations.business;

import data.utils.NullableUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.FxService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IOnceEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.tasks.business.locals.BuildLocalBusinessTask;
import org.flintcore.excel_expenses.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Log4j2
@Component
@Scope("prototype")
public class RegisterLocalBusinessService extends FxService<Boolean>
        implements IEventSubscriptionHandler<WorkerStateEvent, Runnable>,
        IOnceEventSubscriptionHandler<WorkerStateEvent, Runnable> {
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

    public RegisterLocalBusinessService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutdownFXApplication,
            LocalBusinessFileFXService localBusinessFXService,
            LocalBusinessValidator localBusinessValidator) {
        super(eventHandler, shutdownFXApplication);
        this.localBusinessValidator = localBusinessValidator;
        this.localBusinessFXService = localBusinessFXService;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                boolean result = false;

                try {
                    updateMessage("Building Local business...");
                    LocalBusiness localBusiness = new BuildLocalBusinessTask(
                            localBusinessValidator,
                            localNameSupplier,
                            localRNCSupplier
                    ).get();

                    Future<Void> registrationResponse = localBusinessFXService
                            .saveData(localBusiness);

                    updateMessage("Registering business...");

                    try {
                        registrationResponse.get();
                        result = true;
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException("Unable to store data.");
                    }
                } catch (Exception e) {
                    consumeException(e);
                    updateMessage("Fail at buildOf product!");
                } finally {
                    String finallyMessage = result ? BUSINESS_REGISTERED_SUCCESSFULLY
                            : BUSINESS_ALREADY_EXISTS;

                    updateMessage(finallyMessage);
                }

                return result;
            }
        };
    }

    private void consumeException(Exception e) {
        NullableUtils.executeNonNull(this.errorConsumer,
                c -> c.accept(e)
        );
    }
}
