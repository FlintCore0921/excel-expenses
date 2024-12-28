package org.flintcore.excel_expenses.services.internal.py;

import data.utils.NullableUtils;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.business.IBusinessPaginationFxService;
import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.connections.IServerConnection;
import org.flintcore.excel_expenses.services.internal.endpoints.InternalDGIIBusinessEndpointHolder;
import org.flintcore.excel_expenses.services.internal.status.InternalBusinessStatusFxService;
import org.flintcore.excel_expenses.services.models.ServerStatusResponse;
import org.flintcore.excel_expenses.services.requests.business.InternalBusinessService;
import org.flintcore.excel_expenses.services.requests.business.InternalBusinessServiceImpl;
import org.flintcore.excel_expenses.services.status.IFutureServerStatusService;
import org.flintcore.excelib.commons.utilities.FutureHandlerUtils;
import org.flintcore.utilities.runnables.RunnableThrowableOmitted;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Service to communicate with local exe Python service.
 */
@Service
@Profile({"test", "internal-py-server"})
@Log4j2
public class InternalBusinessFxService
        extends InternalBusinessServiceImpl<IPageRequest, LocalBusiness>
        implements IBusinessPaginationFxService<LocalBusiness>,
        IFutureServerStatusService {

    private final InternalBusinessService internalBusinessService;
    private final ReadOnlyBooleanWrapper runningProperty;
    private final ReadOnlyIntegerWrapper requestCounterProperty;
    private final ShutdownFXApplication shutDownHolder;

    protected ObservableSet<LocalBusiness> localBusinesses;

    public InternalBusinessFxService(
            @NonNull Supplier<IServerConnection> serverConnectionBuilder,
            @NonNull RemoteRequestHelper requestHelper,
            @NonNull InternalDGIIBusinessEndpointHolder endpointHolder,
            @NonNull InternalBusinessStatusFxService internalBusinessStatusService,
            @NonNull InternalBusinessService internalBusinessService,
            @NonNull ShutdownFXApplication shutDownHolder
    ) {
        super(serverConnectionBuilder, internalBusinessStatusService, requestHelper, endpointHolder);
        this.internalBusinessService = internalBusinessService;
        this.shutDownHolder = shutDownHolder;

        this.runningProperty = new ReadOnlyBooleanWrapper();
        this.requestCounterProperty = new ReadOnlyIntegerWrapper();
        this.runningProperty.bind(this.requestCounterProperty.greaterThan(0));
    }

    @Override
    public boolean isUp() {
        return this.statusService.isUp();
    }

    @Override
    public Future<ServerStatusResponse> getStatus() {
        return this.statusService.getStatus();
    }

    @Override
    public ReadOnlyBooleanProperty isRequestingProperty() {
        return this.runningProperty.getReadOnlyProperty();
    }

    /**
     * Get all current data get from service.
     */
    @Override
    public Future<ObservableList<LocalBusiness>> getBusinessDataList() {
        this.initDataList();

        return CompletableFuture.completedFuture(
                FXCollections.observableArrayList(this.localBusinesses)
        );
    }

    @Override
    public Future<IPageListResponse<LocalBusiness>> getBusinessDataList(IPageRequest page) {
        this.initDataList();

        assignRequestCount(1);

        return this.internalBusinessService.queueResponse(page)
                .whenComplete((res, th) -> {
                    if (Objects.isNull(th)) {
                        Platform.runLater(() -> this.localBusinesses.addAll(res.getData()));
                    }
                    assignRequestCount(-1);
                });
    }

    /**
     * @see #getBusinessDataList().
     * @see #getBusinessDataList(IPageRequest)
     */
    @Override
    public CompletableFuture<IPageListResponse<LocalBusiness>> queueResponse(IPageRequest pageRequest) {
        return FutureHandlerUtils.asCompletable(getBusinessDataList(pageRequest));
    }

    protected void initDataList() {
        NullableUtils.executeIsNull(this.localBusinesses, () -> {
            // Thread safe and queue set
            this.localBusinesses = FXCollections.observableSet(
                    Collections.synchronizedSet(new LinkedHashSet<>())
            );

            Runnable value = RunnableThrowableOmitted.asRunnable(this.internalBusinessService::close);
            this.shutDownHolder.handle(value);
        });
    }

    private void assignRequestCount(int assignment) {
        int newValue = this.requestCounterProperty.get() + assignment;
        Platform.runLater(() -> this.requestCounterProperty.set(newValue));
    }
}