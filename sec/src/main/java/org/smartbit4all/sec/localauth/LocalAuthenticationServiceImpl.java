package org.smartbit4all.sec.localauth;

import java.net.URI;
import java.util.List;
import java.util.Objects;
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
    Objects.requireNonNull(username, "username can not be null!");
    Objects.requireNonNull(password, "password can not be null!");
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

    if (checkIfAlreadyLoggedIn(session, username)) {
      return;
    }

    User user = orgApi.getUserByUsername(username);

    if (isUserWithPasswordInvalid(user, password)) {

      String reason = "Unknown user or invalid password!";
      if (onLoginFailed != null) {
        onLoginFailed.accept(reason);
      }
      log.debug("Login attempt has failed because of bad credentials with user [{}]", username);
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

  /**
   * Checks if the session already contains an authenticated user. It may check the AccountInfo or
   * if the already logged in username matched the one from the request.<br/>
   * In case of a conflict the overridden methods can throw different exceptions or return true if
   * the login must return immediately without exceptions.
   * 
   * @return if login has to return immediately
   */
  protected boolean checkIfAlreadyLoggedIn(Session session, String username) {
    Objects.requireNonNull(username);
    List<AccountInfo> authentications = session.getAuthentications();
    if (!ObjectUtils.isEmpty(authentications)) {
      AccountInfo foundAccount =
          authentications.stream().filter(a -> KIND.equals(a.getKind())).findAny().orElse(null);
      if (foundAccount != null) {
        log.warn("There is already an authenticated user in this session with kind [{}]!", KIND);
        if (!username.equals(foundAccount.getUserName())) {
          String errorMsg =
              "Trying to log in with the same kind of authentication but with different users!";
          log.error(errorMsg);
          throw new IllegalStateException(errorMsg);
        }
        return true;
      }
    }
    return false;
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
