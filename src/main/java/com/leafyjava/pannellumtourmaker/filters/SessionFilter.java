package com.leafyjava.pannellumtourmaker.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.data.mongo.AbstractMongoSessionConverter;
import org.springframework.session.data.mongo.MongoExpiringSession;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

public class SessionFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFilter.class);

    public static final String ATTR_USER = "auth";
    public static final String COOKIE_NAME = "_s";

    @Value("${application.session.duration}")
    private long sessionDuration;

    private MongoOperationsSessionRepository sessionRepository;

    @Autowired
    public SessionFilter(final MongoOperations mongoOperations,
                         final AbstractMongoSessionConverter sessionConverter) {
        sessionRepository = new MongoOperationsSessionRepository(mongoOperations);
        sessionRepository.setMongoSessionConverter(sessionConverter);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Cookie cookie = WebUtils.getCookie(httpRequest, COOKIE_NAME);

        if (cookie != null) {
            String sessionId = cookie.getValue();
            MongoExpiringSession session = sessionRepository.getSession(sessionId);

            if (session != null && session.getExpireAt().toInstant().isAfter(Instant.now())) {
                Authentication authentication = session.getAttribute(ATTR_USER);

                if (authentication != null) {
                    renewSession(session);

                    SecurityContext sc = SecurityContextHolder.getContext();
                    sc.setAuthentication(authentication);
                    httpRequest.getSession(true)
                        .setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private void renewSession(MongoExpiringSession session) {
        session.setExpireAt(Date.from(Instant.now()
            .plus(sessionDuration, ChronoUnit.MINUTES)));
        sessionRepository.save(session);
    }
}
