package org.flintcore.excel_expenses.services.excels.external;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DGIIApiPropertiesTest.TestConfig.class)
@TestPropertySource(
        value = "classpath:external_dgii_py_test.yaml",
        factory = YamlPropertySourceFactory.class
)
class DGIIApiPropertiesTest {

    @Autowired
    private DGIIApiProperties dgiiApiProperties;

    @Test
    void shouldFoundCorrectValues() {
        assertNotNull(dgiiApiProperties);
        assertTrue(dgiiApiProperties.url().startsWith("http"));
        assertTrue(dgiiApiProperties.port() > 1000);
    }

    @TestConfiguration
    @EnableConfigurationProperties(DGIIApiProperties.class)
    static class TestConfig {
        // Add any additional beans if necessary
    }
}
