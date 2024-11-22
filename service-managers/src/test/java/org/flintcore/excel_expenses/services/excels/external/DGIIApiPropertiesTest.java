package org.flintcore.excel_expenses.services.excels.external;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DGIIApiPropertiesTest.TestConfig.class)
@TestPropertySource(
        value = "classpath:external_dgii_py_test.yaml",
        factory = YamlPropertySourceFactory.class
)
class DGIIApiPropertiesTest {

    @Autowired
    private DGIIAPIProperties dgiiApiProperties;

    @Test
    void shouldFoundCorrectValues() {
        assertNotNull(dgiiApiProperties);
        assertTrue(dgiiApiProperties.url().startsWith("http"));
        assertTrue(dgiiApiProperties.port() > 1000);
        assertNotNull(dgiiApiProperties.timeGapRequest());
        assertEquals(dgiiApiProperties.timeGapRequest().toMinutes(), 10);

        System.out.println(dgiiApiProperties);
    }

    @TestConfiguration
    @EnableConfigurationProperties(DGIIAPIProperties.class)
    static class TestConfig {
        // Add any additional beans if necessary
    }
}
