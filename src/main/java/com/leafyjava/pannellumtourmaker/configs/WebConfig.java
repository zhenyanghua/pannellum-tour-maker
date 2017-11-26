package com.leafyjava.pannellumtourmaker.configs;

import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

    private StorageProperties storageProperties;

    @Autowired
    public WebConfig(final StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/tours/**")
            .addResourceLocations("file://" + storageProperties.getTourLocation() + "/");
        super.addResourceHandlers(registry);
    }
}
