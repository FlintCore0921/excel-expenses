package org.flintcore.excel_expenses.services.configs;

import org.flintcore.excel_expenses.configurations.sources.YamlPropertySourceFactory;
import org.flintcore.excel_expenses.services.configs.properties.InternalDGIIServerProperties;
import org.flintcore.excel_expenses.services.connections.IServerConnection;
import org.flintcore.excel_expenses.services.connections.InternalDGIIServerConnection;
import org.flintcore.excel_expenses.services.internal.endpoints.InternalDGIIBusinessStatusEndpointHolder;
import org.flintcore.excel_expenses.services.internal.endpoints.InternalDGIIBusinessEndpointHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Configuration
@PropertySource(value = "classpath:external_dgii_py.yml",
        factory = YamlPropertySourceFactory.class
)
@EnableConfigurationProperties(value = {
        InternalDGIIServerProperties.class,
})
@Profile({"dev", "test", "internal-py-server"})
public class InternalPyServiceConfiguration {

    // Call for local business from py service
    @Bean
    @Scope("prototype")
    public RestTemplate defaultBuilder(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    @Scope("prototype")
    public Supplier<IServerConnection> internalServerSupplier(
            InternalDGIIServerProperties serverProperties
    ) {
        return () -> InternalDGIIServerConnection.initOn(serverProperties.paths().filePath());
    }

    @Bean
    public InternalDGIIBusinessEndpointHolder internalBusinessEndpointHolder(
            InternalDGIIServerProperties serverProperties
    ) {
        return new InternalDGIIBusinessEndpointHolder(serverProperties);
    }

    @Bean
    public InternalDGIIBusinessStatusEndpointHolder internalBusinessStatusEndpointHolder(
            InternalDGIIServerProperties serverProperties
    ) {
        return new InternalDGIIBusinessStatusEndpointHolder(serverProperties);
    }
}
