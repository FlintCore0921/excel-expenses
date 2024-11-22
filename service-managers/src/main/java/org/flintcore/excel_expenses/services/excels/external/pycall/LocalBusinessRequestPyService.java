package org.flintcore.excel_expenses.services.excels.external.pycall;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.business.IBusinessService;
import org.flintcore.excel_expenses.managers.shutdowns.IShutdownHandler;
import org.flintcore.excel_expenses.managers.subscriptions.tasks.ObservableFXService;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.services.excels.external.DGIIReferenceProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class LocalBusinessRequestPyService extends ObservableFXService<List<LocalBusiness>>
        implements IBusinessService<LocalBusiness> {

    // Time wait data returns / is received.
    private static final long TIME_REQUEST_WAIT = 20;
    private static final TimeUnit TIME_REQUEST_UNIT = TimeUnit.SECONDS;

    // wait prepare service to be ready to send data
    public static final int DELAY_PREPARE_SERVICE = 15;

    private final DGIILocalBusinessPyService apiService;
    private final DGIIReferenceProperties dgiiPathProperties;
    private final IShutdownHandler<Runnable> shutdownHandler;

    private Process pyServiceProcess;

    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {

        Set<Runnable> subscriptionsIn = this.getEventListenerHolder()
                .computeIfAbsent(type, this::buildSubscriptionHolder);
        subscriptionsIn.add(action);

        return () -> subscriptionsIn.remove(action);
    }

    @Override
    public Future<List<LocalBusiness>> getBusinessDataList() {
        CompletableFuture<List<LocalBusiness>> future = new CompletableFuture<>();

        if (!isRunning()) {
            addOneTimeSubscription(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    () -> future.complete(this.getValue())
            );
            this.restart();
        } else future.complete(this.getValue());

        return future;
    }

    @Override
    protected Task<List<LocalBusiness>> createTask() {
        return new Task<>() {
            @Override
            protected List<LocalBusiness> call() {

                List<LocalBusiness> result = null;

                try {
                    initRequestServiceCall();

                    result = apiService.getBusinessDataList()
                            .get(TIME_REQUEST_WAIT, TIME_REQUEST_UNIT);

                } catch (RuntimeException | ExecutionException e) {
                    log.error(e.getMessage(), e);
                    updateMessage("Data not accesable to the moment.");
                } catch (InterruptedException | TimeoutException | IOException e) {
                    updateMessage("Unable to access to data in api: call later.");
                }

                return result;
            }
        };
    }

    private void initRequestServiceCall() throws IOException {
        if (Objects.isNull(this.pyServiceProcess)) return;

        this.pyServiceProcess = new ProcessBuilder(
                this.dgiiPathProperties.py_head(),
                this.dgiiPathProperties.localPath()
        ).start();

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(DELAY_PREPARE_SERVICE));
        } catch (InterruptedException e) {
            this.pyServiceProcess = null;
            throw new IOException(e);
        }

        this.shutdownHandler.addSubscription(this,
                () -> NullableUtils.executeNonNull(this.pyServiceProcess, Process::destroyForcibly)
        );

        if (!this.pyServiceProcess.isAlive()) {
            this.pyServiceProcess.destroyForcibly();
            this.pyServiceProcess = null;
            throw new IOException();
        }
    }

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }
}
