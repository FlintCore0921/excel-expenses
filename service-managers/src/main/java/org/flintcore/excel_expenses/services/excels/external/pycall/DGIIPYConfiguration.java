package org.flintcore.excel_expenses.services.excels.external.pycall;

import org.flintcore.excel_expenses.services.excels.external.DGIIApiProperties;
import org.flintcore.excel_expenses.services.excels.external.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({DGIIApiProperties.class})
@PropertySource(value = "classpath:external_dgii_py.yaml",
        factory = YamlPropertySourceFactory.class
)
public class DGIIPYConfiguration {

    // Call for local business from py service
    @Bean
    public RestTemplate DGIIApiPy(){
        return new RestTemplate();
    }
}
