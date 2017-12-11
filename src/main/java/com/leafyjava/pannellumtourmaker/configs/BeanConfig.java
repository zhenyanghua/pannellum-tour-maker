package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOURS_ADD;
import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOURS_NEW;

@Configuration
public class BeanConfig {

    @Bean
    @Qualifier(TOURS_NEW)
    public Queue toursQueueNew() {
        return new Queue("tours.new");
    }

    @Bean
    @Qualifier(TOURS_ADD)
    public Queue toursQueueAdd() {
        return new Queue("tours.add");
    }
}
