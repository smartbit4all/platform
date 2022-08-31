package org.smartbit4all.sec.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.bean.SessionInfoData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.token.SessionTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This SessionApi implementation manages sessions with the {@link StorageApi} storing the sessions
 * in a persistent way. The user info is gathered by the {@link OrgApi}. <br/>
 * The SID generated on sessionStart contains the URI of the session used when storing the object.
 */
public class SessionApiImpl implements SessionApi {

  private static final Logger log = LoggerFactory.getLogger(SessionApiImpl.class);

  @Autowired
  private SessionTokenHandler tokenHandler;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private OrgApi orgApi;

  @Value("${session.timeout-min:60}")
  private int timeoutMins;

  private Supplier<URI> defaultUserProvider;
  private Supplier<Map<String, String>> defaultParametersProvider;
  private Supplier<String> defaultLocaleProvider;
  private Supplier<List<AccountInfo>> defaultAccountInfoListProvider;

  private Function<URI, Authentication> authenticationTokenProvider;

  private Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null) {
        storageInstance = storageApi.get(SCHEMA);
      }
      return storageInstance;
    }
  };

  @Override
  public SessionInfoData startSession() {

    Session session = createSession();
    URI sessionUri = storage.get().saveAsNew(session);
    log.debug("Session saved!\n{}", session);

    registerSpringSecAuthToken(sessionUri);

    String sid = tokenHandler.createToken(sessionUri.toString(), session.getExpiration());
    log.debug("Session sid created: {}", sid);
    return new SessionInfoData()
        .sid(sid)
        .expiration(session.getExpiration())
        .locale(session.getLocale())
        .authentications(session.getAuthentications());
  }

  private void registerSpringSecAuthToken(URI sessionUri) {
    Authentication authenticationToken = null;
    if (authenticationTokenProvider != null) {
      authenticationToken = authenticationTokenProvider.apply(sessionUri);
      Objects.requireNonNull(authenticationToken, "authenticationTokenProvider has returned null!");
      log.debug(
          "Atuhentication token has been created by the configured authenticationTokenProvider!");
    } else {
      authenticationToken = createAnonymousAuthToken(sessionUri);
      log.debug("Anonymous atuhentication token has been created!");
    }

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  }

  private Authentication createAnonymousAuthToken(URI sessionUri) {
    Authentication authenticationToken;
    SessionAuthPrincipal sessionPrincipal = SessionAuthPrincipal.of(sessionUri);
    authenticationToken = new AnonymousAuthenticationToken(
        "anonymous", sessionPrincipal, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    return authenticationToken;
  }

  private Session createSession() {
    Session session = new Session();

    OffsetDateTime expiration = OffsetDateTime.now().plusMinutes(timeoutMins);
    session.setExpiration(expiration);

    setDefault(session, defaultUserProvider, Session::setUser);
    setDefault(session, defaultParametersProvider, Session::setParameters);
    setDefault(session, defaultLocaleProvider, Session::setLocale);
    setDefault(session, defaultAccountInfoListProvider, Session::setAuthentications);

    log.debug("Session created!\n{}", session);

    return session;
  }

  private <T> void setDefault(Session session, Supplier<T> provider,
      BiConsumer<Session, T> setter) {
    Objects.requireNonNull(session);
    Objects.requireNonNull(setter);
    if (provider != null) {
      setter.accept(session, provider.get());
    }
  }

  @Override
  public User currentUser() {
    Session session = getStoredSessionFromCurrentAuthentication();
    URI userUri = session.getUser();
    if (userUri == null) {
      return null;
    }
    return orgApi.getUser(userUri);
  }

  @Override
  public Session currentSession() {
    return getStoredSessionFromCurrentAuthentication();
  }

  private Session getStoredSessionFromCurrentAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal instanceof SessionAuthPrincipal) {
      URI sessionUri = ((SessionAuthPrincipal) principal).getSessionUri();
      Session session = storage.get().read(sessionUri, Session.class);
      if (session == null) {
        throw new IllegalStateException("There is no current Session available!");
      }
      return session;
    }
    throw new IllegalStateException(
        "The authentication of the security context does not contain a Sb4SessionAuthPrincipal!");
  }

  @Override
  public Session readSession(URI sessionUri) {
    return storage.get().read(sessionUri, Session.class);
  }

  @Override
  public void setSessionParameter(URI sessionUri, String key, String value) {
    updateSession(sessionUri, s -> {
      Map<String, String> parameters = s.getParameters();
      if (parameters == null) {
        parameters = new HashMap<>();
        s.setParameters(parameters);
      }
      parameters.put(key, value);
    });
  }

  @Override
  public void removeSessionParameter(URI sessionUri, String key) {
    updateSession(sessionUri, s -> {
      Map<String, String> parameters = s.getParameters();
      if (parameters == null) {
        parameters = new HashMap<>();
        s.setParameters(parameters);
      }
      parameters.remove(key);
    });

  }

  @Override
  public void setSessionLocale(URI sessionUri, String locale) {
    updateSession(sessionUri, s -> s.setLocale(locale));
  }

  @Override
  public void setSessionExpiration(URI sessionUri, OffsetDateTime expiration) {
    updateSession(sessionUri, s -> s.setExpiration(expiration));
  }

  @Override
  public void addSessionAuthentication(URI sessionUri, AccountInfo accountInfo) {
    updateSession(sessionUri, s -> {
      List<AccountInfo> authentications = s.getAuthentications();
      if (authentications == null) {
        authentications = new ArrayList<>();
        s.setAuthentications(authentications);
      }
      authentications.add(accountInfo);
    });
  }

  @Override
  public void removeSessionAuthentication(URI sessionUri, String kind) {
    updateSession(sessionUri, s -> {
      List<AccountInfo> authentications = s.getAuthentications();
      if (authentications == null) {
        authentications = new ArrayList<>();
        s.setAuthentications(authentications);
      }
      AccountInfo foundInfo = authentications.stream()
          .filter(a -> kind.equals(a.getKind()))
          .findFirst().orElse(null);
      if (foundInfo != null) {
        authentications.remove(foundInfo);
      }
    });
    // TODO remove user uri from session when authentications list gets empty
  }

  @Override
  public void setSessionUser(URI sessionUri, URI userUri) {
    updateSession(sessionUri, s -> s.setUser(userUri));
  }

  private void updateSession(URI sessionUri, Consumer<Session> update) {
    // TODO change to update:
    // storage.get().update(sessionUri, Session.class, null);
    StorageObject<Session> sessionObj = storage.get().load(sessionUri, Session.class);
    Session session = sessionObj.getObject();

    update.accept(session);

    storage.get().save(sessionObj);
  }

  /**
   * Registers a supplier that can provide a {@link User} uri which will be set to every session as
   * default on session start.
   * 
   * @param defaultUserProvider The User uri supplier method
   */
  public void setDefaultUserProvider(Supplier<URI> defaultUserProvider) {
    this.defaultUserProvider = defaultUserProvider;
  }

  /**
   * Registers a supplier that provides a default parameter map that will be set to every session on
   * session start.
   * 
   * @param defaultParametersProvider The default parameter supplier method.
   */
  public void setDefaultParametersProvider(
      Supplier<Map<String, String>> defaultParametersProvider) {
    this.defaultParametersProvider = defaultParametersProvider;
  }

  /**
   * Registers a supplier that provides a default Locale string that will be set to every session on
   * session start.
   * 
   * @param defaultLocaleProvider The default locale string supplier method.
   */
  public void setDefaultLocaleProvider(Supplier<String> defaultLocaleProvider) {
    this.defaultLocaleProvider = defaultLocaleProvider;
  }

  /**
   * Registers a supplier that provides a default {@link AccountInfo} list that will be set to every
   * session on session start.
   * 
   * @param defaultAuthenticationInfoListProvider The default account info list supplier method.
   */
  public void setDefaultAccountInfoListProvider(
      Supplier<List<AccountInfo>> defaultAuthenticationInfoListProvider) {
    this.defaultAccountInfoListProvider = defaultAuthenticationInfoListProvider;
  }

  /**
   * On session start an {@link Authentication} token is registered in the spring security context.
   * By default it is an {@link AnonymousAuthenticationToken}, but with this method it can be
   * configured dynamically.
   * 
   * The Principal of the provided Authentication Token must be a {@link SessionAuthPrincipal}!
   * 
   * @param authenticationTokenProvider The authentication token provider that can use the session
   *        uri to create the token.
   */
  public void setAuthenticationTokenProvider(
      Function<URI, Authentication> authenticationTokenProvider) {
    this.authenticationTokenProvider = authenticationTokenProvider;
  }
}
