package org.smartbit4all.api.session.restserver.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

public class FilterChainExceptionHandler extends OncePerRequestFilter {

  private final Logger log = LoggerFactory.getLogger(getClass());

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
      log.info(
          "Caught Spring Security Filter Chain Exception. Trying to resolve... Error message: {}",
          e.getMessage());
      log.debug("Error trace:", e);
      resolver.resolveException(request, response, null, e);
    }
  }
}
