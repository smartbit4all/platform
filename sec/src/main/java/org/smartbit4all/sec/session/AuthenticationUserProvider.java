package org.smartbit4all.sec.session;

import org.smartbit4all.api.org.bean.User;
import org.springframework.security.core.Authentication;

/**
 * Provides a functionality to get a smartbit4all user from a Spring Security Context Authentication
 * object.
 * 
 * @see UserSessionApiSecImpl
 */
public interface AuthenticationUserProvider {

  User getUser(Authentication authentication);

  boolean supports(Authentication authentication);

}
