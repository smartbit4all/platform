package org.smartbit4all.sec.authentication;

import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.Session;

public interface AuthenticationDataProvider {

  boolean supports(Session session);

  AuthenticationProviderData getProviderData(Session session);

}
