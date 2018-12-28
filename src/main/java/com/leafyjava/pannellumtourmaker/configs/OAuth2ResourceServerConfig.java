package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import static com.leafyjava.pannellumtourmaker.utils.RoleConstants.ROLE_ADMIN;
import static com.leafyjava.pannellumtourmaker.utils.RoleConstants.ROLE_EDITOR;

@Configuration
@EnableResourceServer
@Order(2)
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/api/v1/**")
                .authorizeRequests()
                    .antMatchers("/api/v1/tasks**",
                        "/api/v1/tours**").hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
}
