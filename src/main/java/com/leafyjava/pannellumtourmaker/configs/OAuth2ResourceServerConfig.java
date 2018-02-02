package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/api/v1/**")
            .authorizeRequests()
                .antMatchers("/api/v1/public/guest/tasks/**").hasRole("EDITOR")
                .antMatchers("/api/v1/public/guest/tours/**").hasRole("EDITOR");
    }

}
