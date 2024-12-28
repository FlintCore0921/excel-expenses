package org.flintcore.excel_expenses.services.connections;

import org.flintcore.excel_expenses.configurations.DefaultBeanConfiguration;
import org.flintcore.excel_expenses.configurations.sources.YamlPropertySourceFactory;
import org.flintcore.excel_expenses.services.configs.properties.InternalDGIIServerProperties;
import static org.junit.jupiter.api.Assertions.*;

import org.flintcore.excel_expenses.services.configs.InternalPyServiceConfiguration;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;


@SpringJUnitConfig(classes = {
        InternalPyServiceConfiguration.class,
        DefaultBeanConfiguration.class,
})
@EnableConfigurationProperties(InternalDGIIServerProperties.class)
@TestPropertySource(value = "classpath:external_dgii_py_test.yml",
        factory = YamlPropertySourceFactory.class
)
class InternalDGIIServerConnectionTest {
    @Autowired
    InternalDGIIServerProperties properties;

    @RepeatedTest(5)
    void shouldInitInternalServer() {
        assertDoesNotThrow(() -> {
            try (var conn = initInternalServerConnection()) {
                // Wait until its connected.
                conn.waitUntil(IServerConnection.State.CONNECTED).get();

                assertTrue(conn.isAlive(), "Connection should be alive");
                assertFalse(conn.isClosed(), "Connection is closed");
            } catch (IOException e) {
                fail("Unexpected exception: %s".formatted(e));
            }
        });
    }

    private InternalDGIIServerConnection initInternalServerConnection() {
        return InternalDGIIServerConnection.initOn(
                properties.paths().filePath()
        );
    }


}