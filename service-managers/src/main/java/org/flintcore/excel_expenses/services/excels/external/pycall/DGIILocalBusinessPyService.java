package org.flintcore.excel_expenses.services.excels.external.pycall;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.services.excels.external.DGIIAPIProperties;
import org.flintcore.excel_expenses.managers.services.business.IBusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Log4j2
public class DGIILocalBusinessPyService implements IBusinessService<LocalBusiness> {

    private final DGIIAPIProperties apiProperties;
    private final RestTemplate callPyApi;

    private List<LocalBusiness> businessDataList;
    private LocalDateTime lastTimeRequest;

    public DGIILocalBusinessPyService(
            @Qualifier("DGIIApiPy") RestTemplate CallPyApi,
            DGIIAPIProperties apiProperties
    ) {
        this.apiProperties = apiProperties;
        this.callPyApi = CallPyApi;
    }

    @Override
    public Future<List<LocalBusiness>> getBusinessDataList() {
        LocalDateTime currentTime = LocalDateTime.now();

        Duration gapLastRequest = Duration.between(currentTime, lastTimeRequest);
        // If has enough time to next request, just update.
        if (gapLastRequest.compareTo(apiProperties.timeGapRequest()) <= 0) {
            return CompletableFuture.completedFuture(businessDataList);
        }

        return CompletableFuture.supplyAsync(
                () -> callPyApi.exchange(
                        apiProperties.url(), HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<LocalBusiness>>() {
                        }
                )
        ).thenApply(response -> {
            this.lastTimeRequest = currentTime;
            return this.businessDataList = response.getBody();
        });
    }
}
