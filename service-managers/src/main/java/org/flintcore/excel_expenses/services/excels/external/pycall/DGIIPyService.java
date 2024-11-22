package org.flintcore.excel_expenses.services.excels.external.pycall;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.services.excels.external.DGIIApiProperties;
import org.flintcore.excel_expenses.services.excels.external.ILocalBusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Log4j2
public class DGIIPyService implements ILocalBusinessService<LocalBusiness> {

    private final DGIIApiProperties apiProperties;
    private final RestTemplate callPyApi;

    public DGIIPyService(
            @Qualifier("DGIIApiPy") RestTemplate CallPyApi,
            DGIIApiProperties apiProperties
    ) {
        this.apiProperties = apiProperties;
        this.callPyApi = CallPyApi;
    }

    @Override
    public Future<List<LocalBusiness>> getBusinessDataList() {
        return CompletableFuture.supplyAsync(
                () -> callPyApi.exchange(
                        apiProperties.url(), HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<LocalBusiness>>() {
                        }
                )
        ).thenApply(ResponseEntity::getBody);
    }
}
