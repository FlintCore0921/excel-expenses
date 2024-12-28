package org.flintcore.excel_expenses.services.internal.py;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.flintcore.excel_expenses.configurations.DefaultBeanConfiguration;
import org.flintcore.excel_expenses.configurations.sources.YamlPropertySourceFactory;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.configs.properties.InternalDGIIServerProperties;
import org.flintcore.excel_expenses.services.configs.InternalPyServiceConfiguration;
import org.flintcore.excel_expenses.services.configs.InternalDGIIServerConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        InternalDGIIServerConfiguration.class,
        InternalPyServiceConfiguration.class,
        DefaultBeanConfiguration.class,
        InternalBusinessFxService.class,
        RemoteRequestHelper.class,
        GeneralEventSubscriptionHandler.class,
        ShutdownFXApplication.class,
        CompoundResourceBundle.class,
})
@EnableConfigurationProperties(InternalDGIIServerProperties.class)
@TestPropertySource(value = "classpath:external_dgii_py_test.yml",
        factory = YamlPropertySourceFactory.class
)
@ActiveProfiles("test")
class InternalBusinessFxServiceTest {

    public static final int TIMEOUT = 10;
    public static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;
    @Autowired
    private InternalBusinessFxService internalBusinessFxService;

    @BeforeAll
    static void beforeAll() {
        // Initialize JavaFX environment for tests
        Platform.startup(() -> {
        });
    }

    @Test
    void shouldOpenServer() {
        assertNotNull(internalBusinessFxService);
    }

    @Test
    void shouldRequestFromEndpoint() {
        assertDoesNotThrow(() -> {
            var serviceStatus = this.internalBusinessFxService.getStatus().get();

            assertNotNull(serviceStatus);
            assertTrue(serviceStatus.status());
        });
    }

    @Test
    void shouldReturnDataFromService() {
        assertDoesNotThrow(() -> {

            var businessDataList = this.internalBusinessFxService.getBusinessDataList()
                    .get(TIMEOUT, TIMEOUT_UNIT);

            assertNotNull(businessDataList);
            System.out.println("businessDataList = " + businessDataList);
        });
    }

    @Test
    void testVanillaRequestService() {
        assertDoesNotThrow(() -> {
            Future<ObservableList<LocalBusiness>> futureResponse = this.internalBusinessFxService
                    .getBusinessDataList();

            var listedResult = futureResponse.get(TIMEOUT, TIMEOUT_UNIT);

            assertNotNull(listedResult);
            assertFalse(listedResult.isEmpty());

            System.out.println("listedResult = " + listedResult);
        });
    }
}