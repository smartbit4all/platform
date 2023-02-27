package org.smartbit4all.api.session.restserver.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.exception.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

public class FilterChainExceptionHandler extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(FilterChainExceptionHandler.class);


  @Autowired
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver resolver;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    try {
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      String msg = "Caught Spring Security Filter Chain Exception. Trying to resolve...";
      if (e instanceof SessionException) {
        log.debug(msg, e);
      } else {
        log.warn(msg, e);
      }
      resolver.resolveException(request, response, null, e);
    }
  }
}
