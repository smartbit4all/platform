package org.smartbit4all.sec.localauth;

import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LocalAuthenticationServiceImpl implements LocalAuthenticationService {

  private static final Logger log = LoggerFactory.getLogger(LocalAuthenticationServiceImpl.class);

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private AuthenticationManager authenticationManager;

  private Consumer<AccountInfo> onLogout;

  @Override
  public void login(String username, String password) throws Exception {
    Objects.requireNonNull(username, "username can not be null!");
    Objects.requireNonNull(password, "password can not be null!");

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }

  @Override
  public void logout() {
    URI sessionUri = sessionApi.getSessionUri();
    AccountInfo foundInfo = sessionApi.getAuthentication(KIND);
    if (foundInfo == null) {
      log.warn("Trying to logout from authentication kind [{}], but never was logged in!", KIND);
      return;
    }

    sessionManagementApi.removeSessionAuthentication(sessionUri, foundInfo.getKind());
    if (onLogout != null) {
      onLogout.accept(foundInfo);
    }
  }

  public void onLogout(Consumer<AccountInfo> onLogout) {
    this.onLogout = onLogout;
  }

}
