package com.leafyjava.pannellumtourmaker.configs;

import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.file.Paths;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

    public static final String TOURS = "resources/tours";

    private StorageProperties storageProperties;

    @Autowired
    public WebConfig(final StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + TOURS + "/**")
            .addResourceLocations(Paths.get(storageProperties.getTourLocation()).toUri().toString());
        registry.addResourceHandler("/tour-editor/webjars/**")
            .addResourceLocations("/webjars/");
        registry.addResourceHandler("/tour-editor/js/**")
            .addResourceLocations("/js/");
        registry.addResourceHandler("/tour-editor/css/**")
            .addResourceLocations("/css/");
        registry.addResourceHandler("/tour-editor/img/**")
            .addResourceLocations("/img/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*");
        super.addCorsMappings(registry);
    }
}
