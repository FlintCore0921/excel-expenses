package org.flintcore.excel_expenses.services.excels.external;

import jakarta.annotation.Nullable;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

// Read YAML FILES for resources
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        // Use YamlPropertySourceLoader to load the YAML file
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        Resource resourceResource = resource.getResource();

        return loader.load(name != null ? name : resourceResource.getFilename(), resourceResource).get(0);
    }
}

