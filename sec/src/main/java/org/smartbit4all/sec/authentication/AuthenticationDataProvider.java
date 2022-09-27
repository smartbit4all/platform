package org.smartbit4all.sec.authentication;

import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.Session;

/**
 * Using platform session authentications, configuring {@link AuthenticationDataProvider}
 * implementations makes clients connecting to the BFF request the provided authentication methods.
 */
public interface AuthenticationDataProvider {

  boolean supports(Session session);

  AuthenticationProviderData getProviderData(Session session);

}
