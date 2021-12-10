package org.smartbit4all.sec.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Returns a smartbit4all User based on the principal of the current Spring Security Context. </br>
 * At configuration time {@link AuthenticationUserProvider}s shall be added that can handle the
 * principal objects created by the application's authentication methods.
 */
public class UserSessionApiSecImpl implements UserSessionApi {

  private List<AuthenticationUserProvider> userProviders = new ArrayList<>();

  public UserSessionApiSecImpl(AuthenticationUserProvider authenticationUserProvider,
      AuthenticationUserProvider... authenticationUserProviders) {

    Objects.requireNonNull(authenticationUserProvider,
        "authenticationUserProvider can not be bull!");

    userProviders.add(authenticationUserProvider);
    if (authenticationUserProviders != null && authenticationUserProviders.length > 0) {
      userProviders.addAll(Arrays.asList(authenticationUserProviders));
    }
  }

  @Override
  public User currentUser() {
    Authentication currentAuthentication = getCurrentAuthentication();
    AuthenticationUserProvider userProvider = findUserProvider(currentAuthentication);
    return userProvider.getUser(currentAuthentication);
  }

  @Override
  public Session currentSession() {
    Authentication currentAuthentication = getCurrentAuthentication();
    AuthenticationUserProvider userProvider = findUserProvider(currentAuthentication);
    return userProvider.getSession(currentAuthentication);
  }

  private AuthenticationUserProvider findUserProvider(Authentication currentAuthentication) {
    if (userProviders.isEmpty()) {
      throw new IllegalStateException("There is no AutenticationUserProviders registered!");
    }

    if (currentAuthentication == null) {
      throw new IllegalStateException("There is no current authentication int the context!!");
    }

    AuthenticationUserProvider userProvider = userProviders.stream()
        .filter(up -> up.supports(currentAuthentication))
        .findFirst()
        .orElse(null);

    if (userProvider == null) {
      throw new IllegalStateException("There is no AutenticationUserProviders registered for the"
          + "current Authentication properties!");
    }
    return userProvider;
  }

  private Authentication getCurrentAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public void addAuthenticationUserProvider(AuthenticationUserProvider authenticationUserProvider) {
    userProviders.add(authenticationUserProvider);
  }

  @Override
  public Session startSession(User user) {
    return new Session().user(user);
  }

}
