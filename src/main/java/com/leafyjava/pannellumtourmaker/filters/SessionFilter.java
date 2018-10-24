package com.leafyjava.pannellumtourmaker.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;
import org.springframework.session.data.mongo.AbstractMongoSessionConverter;
import org.springframework.session.data.mongo.MongoOperationsSessionRepository;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

public class SessionFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFilter.class);

    private MongoOperations mongoOperations;
    private AbstractMongoSessionConverter sessionConverter;

    public SessionFilter(final MongoOperations mongoOperations, final AbstractMongoSessionConverter sessionConverter) {
        this.mongoOperations = mongoOperations;
        this.sessionConverter = sessionConverter;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        MongoOperationsSessionRepository sessionRepository =
            new MongoOperationsSessionRepository(mongoOperations);
        sessionRepository.setMongoSessionConverter(sessionConverter);

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Cookie cookie = WebUtils.getCookie(httpRequest, "_s");
        if (cookie != null) {
            String sessionId = cookie.getValue();
//        String sessionId = httpRequest.getParameter("_s");
            Session session = sessionRepository.getSession(sessionId);
            if (session != null) {
                Authentication authentication = session.getAttribute("auth");
                SecurityContext sc = SecurityContextHolder.getContext();
                sc.setAuthentication(authentication);
                httpRequest.getSession(true).setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
