package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/tours/**")
            .addResourceLocations("file:///Users/hua/IdeaProjects/pannellum-tour-maker-service/upload-dir/extracted/");
        super.addResourceHandlers(registry);
    }
}
