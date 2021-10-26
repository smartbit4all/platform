package org.smartbit4all.sec.session;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Returns a smartbit4all User based on the principal of the current Spring Security Context. </br>
 * At configuration time {@link AuthenticationUserProvider}s shall be added that can handle the
 * principal objects created by the application's authentication methods.
 */
public class UserSessionApiSecImpl implements UserSessionApi {

  private List<AuthenticationUserProvider> userProviders = new ArrayList<>();

  private StorageApi storageApi;

  private Storage sessionStorage;

  public UserSessionApiSecImpl(StorageApi storageApi,
      AuthenticationUserProvider authenticationUserProvider,
      AuthenticationUserProvider... authenticationUserProviders) {

    Objects.requireNonNull(authenticationUserProvider,
        "authenticationUserProvider can not be bull!");

    this.storageApi = storageApi;
    userProviders.add(authenticationUserProvider);
    if (authenticationUserProviders != null && authenticationUserProviders.length > 0) {
      userProviders.addAll(Arrays.asList(authenticationUserProviders));
    }
  }

  private void initSessionStorage() {
    if (sessionStorage == null) {
      sessionStorage = storageApi.get(UserSessionApi.SCHEME);
    }
  }

  @Override
  public User currentUser() {
    if (userProviders.isEmpty()) {
      throw new IllegalStateException("There is no AutenticationUserProviders registered!");
    }

    Authentication currentAuthentication = getCurrentAuthentication();
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

    return userProvider.getUser(currentAuthentication);
  }

  private Authentication getCurrentAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public void addAuthenticationUserProvider(AuthenticationUserProvider authenticationUserProvider) {
    userProviders.add(authenticationUserProvider);
  }

  @Override
  public StorageObject<Session> startSession(URI userUri) {
    initSessionStorage();
    StorageObject<Session> sessionSO = sessionStorage.instanceOf(Session.class);
    Session session = new Session().userUri(userUri);
    sessionSO.setObject(session);
    saveSession(sessionSO);
    return sessionSO;
  }

  @Override
  public void saveSession(StorageObject<Session> session) {
    sessionStorage.save(session);
  }

  @Override
  public StorageObject<Session> currentSession() {
    Authentication currentAuthentication = getCurrentAuthentication();
    if (currentAuthentication == null) {
      throw new IllegalStateException("There is no current authentication int the context!!");
    }
    if (currentAuthentication.getPrincipal() == null ||
        !(currentAuthentication.getPrincipal() instanceof Session)) {
      throw new IllegalStateException("Current authentication is not Session!");
    }
    URI sessionUri = ((Session) currentAuthentication.getPrincipal()).getUri();
    return sessionStorage.exists(sessionUri) ? sessionStorage.load(sessionUri, Session.class)
        : null;
  }

}
