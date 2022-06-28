package org.smartbit4all.sec.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtRequestFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private UserSessionApi userSessionApi;

  @Autowired
  private JwtUtil jwtUtil;

  @Value("${jwt.cookie}")
  private String jwtCookie;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    /**
     * This method filters the incoming REST API calls by the authentication in the header. If the
     * header contains a valid JwtToken (it is not expired and a user can be decrypted) the call
     * continues to execution. However, if the JwtToken is not valid, it throws an exception and the
     * call would not be executed.
     */
    final String authorizationHeader = request.getHeader("Authorization");

    String username = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      username = jwtUtil.extractUsername(jwt);
    } else {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
          Cookie cookie = cookies[i];
          if (cookie != null && jwtCookie.equals(cookie.getName())) {
            jwt = cookie.getValue();
            username = jwtUtil.extractUsername(jwt);
          }
        }
      }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      User user = this.orgApi.getUserByUsername(username);
      if (jwtUtil.validateToken(jwt, user)) {
        Session session = userSessionApi.getSessionByToken(jwt);
        if (session != null) {
          Authentication authentication =
              (Authentication) session.getParameter(Authentication.class.getName());
          if (authentication != null) {
            if (AbstractAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
              ((AbstractAuthenticationToken) authentication)
                  .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            } else {
              log.warn("The given authentication token [{}] is not a subclass of {}, "
                  + "thus details can not be set",
                  authentication.getClass().getName(), AbstractAuthenticationToken.class.getName());
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
