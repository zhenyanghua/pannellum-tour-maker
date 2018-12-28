package com.leafyjava.pannellumtourmaker.configs;

import com.leafyjava.pannellumtourmaker.exceptions.MvcAccessDeniedHandler;
import com.leafyjava.pannellumtourmaker.exceptions.MvcAuthenticationEntryPoint;
import com.leafyjava.pannellumtourmaker.filters.SessionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.session.data.mongo.AbstractMongoSessionConverter;

import javax.servlet.Filter;

import static com.leafyjava.pannellumtourmaker.utils.RoleConstants.ROLE_ADMIN;
import static com.leafyjava.pannellumtourmaker.utils.RoleConstants.ROLE_EDITOR;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final MongoOperations mongoOperations;
    private final AbstractMongoSessionConverter sessionConverter;

    @Autowired
    public WebSecurityConfig(final MongoOperations mongoOperations,
                             final AbstractMongoSessionConverter sessionConverter) {
        this.mongoOperations = mongoOperations;
        this.sessionConverter = sessionConverter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/app/**")
                .addFilterBefore(sessionFilter(), SecurityContextPersistenceFilter.class)
                .authorizeRequests()
                .antMatchers("/app/js/**", "/app/css/**", "/app/img/**").permitAll()
                .anyRequest().hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)
            .and()
                .antMatcher("/resources/**")
                .addFilterBefore(sessionFilter(), SecurityContextPersistenceFilter.class)
                .authorizeRequests()
                .anyRequest().hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)
            .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler())
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }

    @Bean
    public Filter sessionFilter() {
        return new SessionFilter(mongoOperations, sessionConverter);
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
