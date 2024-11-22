package org.flintcore.excel_expenses.services.excels.external.pycall;

import org.flintcore.excel_expenses.services.excels.external.DGIIApiProperties;
import org.flintcore.excel_expenses.services.excels.external.YamlPropertySourceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        DGIIPyService.class
})
@EnableConfigurationProperties(value = DGIIApiProperties.class)
@TestPropertySource(
        value = "classpath:external_dgii_py_test.yaml",
        factory = YamlPropertySourceFactory.class
)
class DGIIPyServiceTest {
    @Autowired
    private DGIIPyService service;

    @Test
    void shouldBuildApplication() {
        assertNotNull(service);

        assertNotNull(service.getBusinessDataList());
    }
}