package com.rate.rate_limit_api.filter;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RequestThrottleFilter implements Filter {

    Logger logger = Logger.getLogger(RequestThrottleFilter.class);

    @Value("${per.user.rate.limit}")
    private int MAX_REQUESTS;

    private final LoadingCache<String, Integer> requestCountsPerIpAddress;

    public RequestThrottleFilter() {
        super();
        requestCountsPerIpAddress = Caffeine.newBuilder().
                expireAfterWrite(60*60, TimeUnit.SECONDS).build(key -> 0);
    }

    @Override
    public void init(FilterConfig filterConfig)  {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String clientMailAddress = "arshad@gmail.com";

        if (!httpServletRequest.getMethod().matches("POST|PUT|DELETE")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
                if (isMaximumRequestsPerSecondExceeded(clientMailAddress)) {
                    logger.info("Too Many requests from User: "+clientMailAddress);
                    httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    httpServletResponse.getWriter().write("Too many requests");
                    return;
                }
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isMaximumRequestsPerSecondExceeded(String clientMailAddress) {
        Integer requests = 0;
        requests = requestCountsPerIpAddress.get(clientMailAddress);
        if (requests > MAX_REQUESTS) {
            return true;
        }
        requests++;
        requestCountsPerIpAddress.put(clientMailAddress, requests);
        return false;
    }

    @Override
    public void destroy() {

    }
}