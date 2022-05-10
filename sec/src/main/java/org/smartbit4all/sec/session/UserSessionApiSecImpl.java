package org.smartbit4all.sec.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.google.common.base.Strings;

/**
 * Returns a smartbit4all User based on the principal of the current Spring Security Context. </br>
 * At configuration time {@link AuthenticationUserProvider}s shall be added that can handle the
 * principal objects created by the application's authentication methods.
 */
public class UserSessionApiSecImpl implements UserSessionApi {


  private static final Logger log = LoggerFactory.getLogger(UserSessionApiSecImpl.class);

  private static final String SESSION_TOKEN = "UserSessionApiSecImpl.SESSION_TOKEN";

  private List<AuthenticationUserProvider> userProviders = new ArrayList<>();

  private AuthenticationUserProvider technicalUserProvider;

  private Map<String, Session> savedSessions = new HashMap<>();

  public void setTechnicalUserProvider(AuthenticationUserProvider technicalUserProvider) {
    this.technicalUserProvider = technicalUserProvider;
  }

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
      throw new IllegalStateException("There is no current authentication in the context!!");
    }

    AuthenticationUserProvider userProvider = userProviders.stream()
        .filter(up -> up.supports(currentAuthentication))
        .findFirst()
        .orElse(null);

    if (userProvider == null) {
      throw new IllegalStateException("There is no AuthenticationUserProvider registered for the "
          + "current Authentication class!" + currentAuthentication.getClass().getName());
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
    return new Session(new ObjectChangePublisherSpringSecAware<>()).user(user);
  }

  @Override
  public Session startSession(User user, String token) {
    Session session = startSession(user);
    storeSession(token, session);
    return session;
  }

  @Override
  public void storeCurrentSession(String token) {
    storeSession(token, currentSession());
  }

  private void storeSession(String token, Session session) {
    if (session != null) {
      if (savedSessions.containsKey(token)) {
        String originalToken = (String) session.getParameter(SESSION_TOKEN);
        log.warn(
            "storeCurrentSession will override an existing Session with token {} Original token: {}",
            token, originalToken);
      }
      session.setParameter(SESSION_TOKEN, token);
      savedSessions.put(token, session);
    } else {
      log.warn("storeCurrentSession called when session is not present");
    }
  }

  @Override
  public Session getSessionByToken(String token) {
    return savedSessions.get(token);
  }

  @Override
  public void removeCurrentSession() {
    Session currentSession = currentSession();
    if (currentSession != null) {
      String token = (String) currentSession.getParameter(SESSION_TOKEN);
      if (!Strings.isNullOrEmpty(token)) {
        if (savedSessions.containsKey(token)) {
          savedSessions.remove(token);
        } else {
          log.warn(
              "removeCurrentSession called but current session is not stored. token: {}", token);
        }
      } else {
        log.warn("removeCurrentSession called but current session doesnt have a token");
      }
    } else {
      log.warn("removeCurrentSession called when session is not present");
    }
  }

}
