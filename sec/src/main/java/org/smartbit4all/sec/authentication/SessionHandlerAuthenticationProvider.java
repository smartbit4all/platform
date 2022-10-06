package org.smartbit4all.sec.authentication;

import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;
import org.smartbit4all.sec.authprincipal.SessionAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

public abstract class SessionHandlerAuthenticationProvider implements AuthenticationProvider {

  private static final Logger log =
      LoggerFactory.getLogger(SessionHandlerAuthenticationProvider.class);

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private OrgApi orgApi;

  private BiFunction<User, Authentication, AccountInfo> accountInfoProvider =
      defaultAccountInfoProvider();

  private final AuthenticationProvider wrappedAuthenticationProvider;

  private final String accountKind;

  private boolean isAccountOverrideForbidden = false;

  private AdditionalSessionAuthenticationChecker additionalAuthenticationCheck =
      (u, ai, oa) -> {};

  private Predicate<Class<?>> additionalSupportCheck = a -> true;

  private Consumer<String> onLoginFailed = msg -> {
  };
  private Consumer<User> onLoginSucceeded = u -> {
  };

  protected SessionHandlerAuthenticationProvider(String accountKind,
      AuthenticationProvider wrappedAuthenticationProvider) {
    Assert.notNull(wrappedAuthenticationProvider, "wrappedAuthenticationProvider cannot be null");
    Assert.notNull(accountKind, "kind cannot be null");
    this.accountKind = accountKind;
    this.wrappedAuthenticationProvider = wrappedAuthenticationProvider;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    URI sessionUri = null;
    try {
      sessionUri = sessionApi.getSessionUri();
    } catch (NoCurrentSessionException e) {
      String reason = "There is no session available while trying to login!";
      onLoginFailed.accept(reason);
      throw new IllegalStateException(reason, e);
    }

    AccountInfo foundMatchingAccount = sessionApi.getAuthentication(accountKind);
    if (foundMatchingAccount != null) {
      log.warn("There is already an authenticated account in this session with kind [{}]!",
          accountKind);
      if (isAccountOverrideForbidden) {
        throw new IllegalStateException(
            "Can not log in with account if it is already present in the session!");
      }
    }

    Authentication originalAuthentication = null;
    try {
      originalAuthentication = wrappedAuthenticationProvider.authenticate(authentication);
    } catch (AuthenticationException e) {
      log.debug("Login attempt has failed!", e);
      onLoginFailed.accept(e.getMessage());
      throw e;
    }

    User user = getUserFromAuthentication(originalAuthentication);
    if (user == null) {
      throw new IllegalStateException(
          "User could not obtained from the original authentication token!");
    }

    AccountInfo accountInfo = accountInfoProvider.apply(user, originalAuthentication);
    accountInfo.setKind(accountKind);

    try {
      additionalAuthenticationCheck.check(user, accountInfo, originalAuthentication);
    } catch (AuthenticationException e) {
      log.debug("Login attempt has failed on additional authentication checks!", e);
      onLoginFailed.accept(e.getMessage());
      throw e;
    }

    if (foundMatchingAccount != null
        && !Objects.equals(foundMatchingAccount.getUserName(), accountInfo.getUserName())) {
      String errorMsg =
          "Trying to log in with the same kind of authentication but with different users!";
      log.error(errorMsg);
      throw new IllegalStateException(errorMsg);
    }

    sessionManagementApi.setSessionUser(sessionUri, user.getUri());
    sessionManagementApi.addSessionAuthentication(sessionUri, accountInfo);

    onLoginSucceeded.accept(user);

    return createSessionAuthentication(originalAuthentication, user, sessionUri,
        sessionApi.getAuthentications());
  }


  protected SessionAuthToken createSessionAuthentication(
      Authentication originalAuthentication, User user, URI sessionUri,
      Collection<AccountInfo> accountInfos) {
    SessionAuthToken sessionAuthToken = SessionAuthToken.create(sessionUri, accountInfos);
    sessionAuthToken.setOrigin(originalAuthentication);
    return sessionAuthToken;
  }

  protected abstract User getUserFromAuthentication(Authentication originalAuthentication);

  @Override
  public boolean supports(Class<?> authentication) {
    return wrappedAuthenticationProvider.supports(authentication)
        && additionalSupportCheck.test(authentication);
  }

  public void setAdditionalSupportCheck(Predicate<Class<?>> additionalSupportCheck) {
    Assert.notNull(additionalSupportCheck, "additionalSupportCheck cannot be null");
    this.additionalSupportCheck = additionalSupportCheck;
  }

  public void setAdditionalAuthenticationCheck(
      AdditionalSessionAuthenticationChecker additionalAuthenticationCheck) {
    Assert.notNull(additionalAuthenticationCheck, "additionalAuthenticationCheck cannot be null");
    this.additionalAuthenticationCheck = additionalAuthenticationCheck;
  }

  public void onLoginFailed(Consumer<String> onLoginFailed) {
    Assert.notNull(onLoginFailed, "onLoginFailed cannot be null");
    this.onLoginFailed = onLoginFailed;
  }

  public void onLoginSucceeded(Consumer<User> onLoginSucceeded) {
    Assert.notNull(onLoginSucceeded, "onLoginSucceeded cannot be null");
    this.onLoginSucceeded = onLoginSucceeded;
  }

  public boolean isAccountOverrideForbidden() {
    return isAccountOverrideForbidden;
  }

  public void setAccountOverrideForbidden(boolean isAccountOverrideForbidden) {
    this.isAccountOverrideForbidden = isAccountOverrideForbidden;
  }

  public void setAccountInfoProvider(
      BiFunction<User, Authentication, AccountInfo> accountInfoProvider) {
    this.accountInfoProvider = accountInfoProvider;
  }

  protected final SessionApi sessionApi() {
    return sessionApi;
  }

  protected final SessionManagementApi sessionManagementApi() {
    return sessionManagementApi;
  }

  private BiFunction<User, Authentication, AccountInfo> defaultAccountInfoProvider() {
    return (user, originalAuthToken) -> new AccountInfo()
        .userName(user.getUsername())
        .displayName(user.getName())
        .roles(orgApi.getGroupsOfUser(user.getUri()).stream()
            .map(Group::getName)
            .collect(Collectors.toList()))
        .parameters(user.getAttributes());

  }

  public static interface AdditionalSessionAuthenticationChecker {
    void check(User user, AccountInfo accountInfo, Authentication originalAuthentication)
        throws AuthenticationException;
  }

}
