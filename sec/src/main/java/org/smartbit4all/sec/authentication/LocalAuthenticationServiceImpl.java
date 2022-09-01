package org.smartbit4all.sec.authentication;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

public class LocalAuthenticationServiceImpl implements LocalAuthenticationService {

  private static final Logger log = LoggerFactory.getLogger(LocalAuthenticationServiceImpl.class);

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private SessionApi sessionApi;

  private Function<User, AccountInfo> accountInfoProvider;

  private Consumer<String> onLoginFailed;
  private Consumer<User> onLoginSucceeded;
  private Consumer<AccountInfo> onLogout;

  public LocalAuthenticationServiceImpl(Function<User, AccountInfo> accountInfoProvider) {
    this.accountInfoProvider = accountInfoProvider;
  }

  @Override
  public void login(String username, String password) throws Exception {
    Session session = sessionApi.currentSession();
    if (session == null) {
      String reason = "There is no session available while trying to login!";
      if (onLoginFailed != null) {
        onLoginFailed.accept(reason);
      }
      throw new IllegalStateException(reason);
    }
    log.debug(
        "Trying to log in with user [{}] and authentication kind [{}] for the following session:\n{}",
        username, KIND, session);


    User user = orgApi.getUserByUsername(username);

    if (isUserWithPasswordInvalid(user, password)) {

      String reason = "Unknown user or invalid password!";
      if (onLoginFailed != null) {
        onLoginFailed.accept(reason);
      }
      throw new BadCredentialsException(reason);
    }

    URI sessionUri = session.getUri();
    sessionApi.setSessionUser(sessionUri, user.getUri());

    AccountInfo accountInfo = accountInfoProvider.apply(user);
    accountInfo.setKind(KIND);
    sessionApi.addSessionAuthentication(sessionUri, accountInfo);
    if (onLoginSucceeded != null) {
      onLoginSucceeded.accept(user);
    }

    // FIXME store in security context?
  }

  protected boolean isUserWithPasswordInvalid(User user, String password) {
    return user == null
        || ObjectUtils.isEmpty(password)
        || !passwordEncoder.matches(password, user.getPassword());
  }

  @Override
  public void logout() {
    Session session = sessionApi.currentSession();
    List<AccountInfo> authentications = session.getAuthentications();
    if (ObjectUtils.isEmpty(authentications)) {
      log.warn("Trying to logout from authentication kind [{}], but never was logged in!", KIND);
      return;
    }
    AccountInfo foundInfo = authentications.stream()
        .filter(a -> KIND.equals(a.getKind()))
        .findFirst().orElse(null);
    if (foundInfo == null) {
      log.warn("Trying to logout from authentication kind [{}], but never was logged in!", KIND);
      return;
    }

    sessionApi.removeSessionAuthentication(session.getUri(), foundInfo.getKind());
    if (onLogout != null) {
      onLogout.accept(foundInfo);
    }
  }

  public void onLoginFailed(Consumer<String> onLoginFailed) {
    this.onLoginFailed = onLoginFailed;
  }

  public void onLoginSucceeded(Consumer<User> onLoginSucceeded) {
    this.onLoginSucceeded = onLoginSucceeded;
  }

  public void onLogout(Consumer<AccountInfo> onLogout) {
    this.onLogout = onLogout;
  }

}
