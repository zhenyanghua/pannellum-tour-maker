package com.leafyjava.pannellumtourmaker.configs;

import com.leafyjava.pannellumtourmaker.exceptions.MvcAccessDeniedHandler;
import com.leafyjava.pannellumtourmaker.exceptions.MvcAuthenticationEntryPoint;
import com.leafyjava.pannellumtourmaker.filters.SessionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableResourceServer
@Order(2)
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${application.domain}")
    private String domain;

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/api/v1/**")
                .authorizeRequests()
                    .antMatchers("/api/v1/tasks/**").hasAnyRole("EDITOR", "ADMIN")
                    .antMatchers("/api/v1/tours/**").hasAnyRole("EDITOR", "ADMIN")
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new MvcAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new MvcAuthenticationEntryPoint();
    }
}
