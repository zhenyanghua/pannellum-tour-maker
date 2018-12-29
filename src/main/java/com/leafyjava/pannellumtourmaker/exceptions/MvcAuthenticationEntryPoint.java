package com.leafyjava.pannellumtourmaker.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MvcAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MvcAuthenticationEntryPoint.class);

    @Value("${application.domain}")
    private String domain;

    @Value("${spring.application.path}")
    private String path;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
        String user = "Guest user";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            user = authentication.getName();
        }
        LOGGER.warn(String.format("%s attempted to access the protected URL: %s",
            user, request.getRequestURI()));
        response.sendRedirect(String.format("%s/login?from=%s%s", domain, path, request.getRequestURI()));
    }
}
