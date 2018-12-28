package com.leafyjava.pannellumtourmaker.configs;

import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.file.Paths;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

    public static final String TOURS = "resources/tours";

    private StorageProperties storageProperties;

    @Autowired
    public WebConfig(final StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        String tourLocation = Paths.get(storageProperties.getTourLocation()).toUri().toString();
        LOGGER.info(tourLocation);
        registry.addResourceHandler("/" + TOURS + "/**")
            .addResourceLocations(tourLocation);
        registry.addResourceHandler("/app/webjars/**")
            .addResourceLocations("/webjars/");
        registry.addResourceHandler("/app/js/**")
            .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/app/css/**")
            .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/app/img/**")
            .addResourceLocations("classpath:/static/img/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*");
        super.addCorsMappings(registry);
    }

    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
                tomcat.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
            }
        };
    }
}
