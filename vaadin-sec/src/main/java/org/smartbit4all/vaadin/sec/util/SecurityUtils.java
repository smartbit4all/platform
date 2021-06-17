package org.smartbit4all.vaadin.sec.util;

import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;

// https://github.com/vaadin/flow/issues/4212
public final class SecurityUtils {

  private SecurityUtils() {
    // Util methods only
  }

  public static boolean isFrameworkInternalRequest(HttpServletRequest request) {
    final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
    // Ez a Vaadin 14-ben van
    // return parameterValue != null && Stream.of(ServletHelper.RequestType.values())
    return parameterValue != null && Stream.of(HandlerHelper.RequestType.values())
        .anyMatch(r -> r.getIdentifier().equals(parameterValue));
  }

  public static boolean isUserLoggedIn() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
        && authentication.isAuthenticated();
  }
}
