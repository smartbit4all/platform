package org.smartbit4all.sec.token;

import org.smartbit4all.api.session.bean.Session;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Using Spring security an {@link Authentication} needs to be registered to the security context to
 * identify the caller. In sb4 platform the persistent {@link Session} holds all the information
 * needed for identification. On incoming calls the session can be started or detected if already
 * existing. <br/>
 * With this interface an {@link AbstractAuthenticationToken} can be created based on the session
 * information. One system may manage multiple type of authentication tokens based on different
 * authentication types meaning more then one instances of this provider can exist at the same time.
 */
public interface SessionBasedAuthTokenProvider {

  /**
   * Returns if the given session is applicable to create an authentication token with the
   * {@link #getToken(Session)} method.
   */
  boolean supports(Session session);

  /**
   * Returns a {@link AbstractAuthenticationToken} with the information provided in the
   * {@link Session} bean.
   */
  AbstractAuthenticationToken getToken(Session session);

}
