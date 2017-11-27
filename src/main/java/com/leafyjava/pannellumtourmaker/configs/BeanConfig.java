package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    @Qualifier("tours.zip.multires")
    public Queue toursZipMultiresQueue() {
        return new Queue("tours.zip.multires");
    }

    @Bean
    @Qualifier("tours.zip.equirectangular")
    public Queue queue() {
        return new Queue("tours.zip.equirectangular");
    }
}
