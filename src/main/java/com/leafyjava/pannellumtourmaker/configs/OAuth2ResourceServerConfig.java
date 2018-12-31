package com.leafyjava.pannellumtourmaker.configs;

import com.leafyjava.pannellumtourmaker.exceptions.MvcAccessDeniedHandler;
import com.leafyjava.pannellumtourmaker.exceptions.MvcAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

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
                    .antMatchers("/api/v1/{\\w+}/**").hasAnyRole(ROLE_EDITOR, ROLE_ADMIN)
            .and().exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
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
