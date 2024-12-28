package org.flintcore.excel_expenses.configurations.external;

import org.flintcore.excel_expenses.configurations.sources.YamlPropertySourceFactory;
import org.flintcore.excel_expenses.services.configs.properties.InternalDGIIServerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@EnableConfigurationProperties(InternalDGIIServerProperties.class)
@TestPropertySource(value = "classpath:external_dgii_py_test.yml",
        factory = YamlPropertySourceFactory.class
)
class DGIIApiPropertiesTest {

    @Autowired
    private InternalDGIIServerProperties dgiiApiProperties;

    @Test
    void shouldFoundCorrectValues() {
        assertNotNull(dgiiApiProperties);
        assertTrue(dgiiApiProperties.port() > 0);
        assertNotNull(dgiiApiProperties.timeGapRequest());
        assertEquals(10L, dgiiApiProperties.timeGapRequest().toMinutes());
        assertNotNull(dgiiApiProperties.mainUri());

        InternalDGIIServerProperties.Endpoints enpoints = dgiiApiProperties.endpoints();
        assertNotNull(enpoints);
        assertNotNull(enpoints.businessMain());

        InternalDGIIServerProperties.Path path = dgiiApiProperties.paths();
        assertNotNull(path);
        assertNotNull(path.cmdCommand());
        assertNotNull(path.filePath());

        System.out.println(dgiiApiProperties);
    }
}
