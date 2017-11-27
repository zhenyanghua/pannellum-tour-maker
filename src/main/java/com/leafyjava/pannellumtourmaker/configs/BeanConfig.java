package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public Queue queue() {
        return new Queue("tours");
    }
}
