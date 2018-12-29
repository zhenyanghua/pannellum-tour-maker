package com.leafyjava.pannellumtourmaker.configs;

import com.leafyjava.library.commons.filters.SessionFilter;
import com.leafyjava.pannellumtourmaker.exceptions.MvcAccessDeniedHandler;
import com.leafyjava.pannellumtourmaker.exceptions.MvcAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import static com.leafyjava.pannellumtourmaker.utils.RoleConstants.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SessionFilter sessionFilter;

    public WebSecurityConfig(final SessionFilter sessionFilter) {
        this.sessionFilter = sessionFilter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/app/**")
                .addFilterBefore(sessionFilter, SecurityContextPersistenceFilter.class)
                .authorizeRequests()
                .antMatchers("/app/js/**", "/app/css/**", "/app/img/**").permitAll()
                .anyRequest().hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)
            .and()
                .antMatcher("/resources/**")
                .addFilterBefore(sessionFilter, SecurityContextPersistenceFilter.class)
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
    public AccessDeniedHandler accessDeniedHandler() {
        return new MvcAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new MvcAuthenticationEntryPoint();
    }

}
