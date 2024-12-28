package org.flintcore.excel_expenses.configurations.pycall;

import org.flintcore.excel_expenses.configurations.DefaultBeanConfiguration;
import org.flintcore.excel_expenses.configurations.sources.YamlPropertySourceFactory;
import org.flintcore.excel_expenses.services.configs.properties.InternalDGIIServerProperties;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.configs.InternalPyServiceConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        DefaultBeanConfiguration.class,
        InternalPyServiceConfiguration.class,
        RemoteRequestHelper.class
})
@EnableConfigurationProperties(InternalDGIIServerProperties.class)
@TestPropertySource(value = "classpath:external_dgii_py_test.yml",
        factory = YamlPropertySourceFactory.class
)
@ActiveProfiles("test")
class RemoteEndpointsHelperTest {
    @Autowired
    private RemoteRequestHelper service;

    @Test
    void shouldBuildApplication() {
        assertNotNull(service);
    }
}