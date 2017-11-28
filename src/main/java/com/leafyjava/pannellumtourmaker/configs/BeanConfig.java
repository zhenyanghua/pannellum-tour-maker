package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOUR_ZIP_EQUIRECTANGULAR;
import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOUR_ZIP_MULTIRES;

@Configuration
public class BeanConfig {
    @Bean
    @Qualifier(TOUR_ZIP_MULTIRES)
    public Queue toursZipMultiresQueue() {
        return new Queue("tours.zip.multires");
    }

    @Bean
    @Qualifier(TOUR_ZIP_EQUIRECTANGULAR)
    public Queue queue() {
        return new Queue("tours.zip.equirectangular");
    }
}
