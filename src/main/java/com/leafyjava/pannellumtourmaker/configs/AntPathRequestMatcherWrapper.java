package com.leafyjava.pannellumtourmaker.configs;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public abstract class AntPathRequestMatcherWrapper implements RequestMatcher {
    private AntPathRequestMatcher matcher;

    public AntPathRequestMatcherWrapper(final String pattern) {
        this.matcher = new AntPathRequestMatcher(pattern);
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (test(request)) {
            return matcher.matches(request);
        }
        return false;
    }

    protected abstract boolean test(HttpServletRequest request);
}
