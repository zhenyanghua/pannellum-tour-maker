package com.leafyjava.pannellumtourmaker.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MvcAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MvcAccessDeniedHandler.class);

    @Value("${application.domain}")
    private String domain;

    @Value("${spring.application.path}")
    private String path;

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException accessDeniedException) throws IOException {
        String user = "Guest user";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            user = authentication.getName();
        }
        LOGGER.warn(String.format("%s attempted to access the protected URL: %s",
            user, request.getRequestURI()));

        String serverPath = domain + path + "/app";

        response.sendRedirect(String.format("%s/access-denied", serverPath));
    }
}
