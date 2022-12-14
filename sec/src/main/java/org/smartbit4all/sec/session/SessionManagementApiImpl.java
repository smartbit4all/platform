package org.smartbit4all.sec.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.bean.SessionInfoData;
import org.smartbit4all.api.session.exception.InvalidRefreshTokenException;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.authprincipal.SessionAuthToken;
import org.smartbit4all.sec.token.SessionTokenHandler;
import org.smartbit4all.sec.utils.SecurityContextUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * This SessionApi implementation manages sessions with the {@link StorageApi} storing the sessions
 * in a persistent way. The user info is gathered by the {@link OrgApi}. <br/>
 * The SID generated on sessionStart contains the URI of the session used when storing the object.
 */
public class SessionManagementApiImpl implements SessionManagementApi {

  private static final String TECHNICAL = "technical";

  private static final String REFRESHTOKEN_PREFIX = "REFRESHTOKEN_";

  private static final Logger log = LoggerFactory.getLogger(SessionManagementApiImpl.class);

  private static final String EXPMSG_MISSING_SESSIONURI = "sessionUri can not be null!";

  private static final ThreadLocal<Session> currentSession = new ThreadLocal<>();

  @Autowired
  private SessionTokenHandler tokenHandler;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private OrgApi orgApi;

  @Value("${session.timeout-min:60}")
  private int timeoutMins;

  @Value("${session.refresh-timeout-min:120}")
  private int refreshTimeoutMins;

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
        storageInstance.setVersionPolicy(VersionPolicy.SINGLEVERSION);
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

    String refreshToken = createRefreshToken(sessionUri);
    String sid = createSid(sessionUri, session.getExpiration());
    storage.get().update(session.getUri(), Session.class,
        s -> s.putParametersItem(SessionInfoData.SID, sid)
            .putParametersItem(SessionInfoData.REFRESH_TOKEN, refreshToken));

    log.debug("Session sid created: {}", sid);
    return new SessionInfoData()
        .sid(sid)
        .expiration(session.getExpiration())
        .locale(session.getLocale())
        .authentications(session.getAuthentications())
        .refreshToken(refreshToken);
  }

  @Override
  public SessionInfoData refreshSession(String refreshToken) {
    if (!tokenHandler.isTokenValid(refreshToken)
        || !tokenHandler.getSubject(refreshToken).startsWith(REFRESHTOKEN_PREFIX)) {
      throw new InvalidRefreshTokenException();
    }
    String sessionUriTxt =
        tokenHandler.getSubject(refreshToken).substring(REFRESHTOKEN_PREFIX.length());

    Session session = readSession(URI.create(sessionUriTxt));
    if (session == null) {
      throw new InvalidRefreshTokenException();
    }

    OffsetDateTime expiration = OffsetDateTime.now().plusMinutes(timeoutMins);
    setSessionExpiration(session.getUri(), expiration);

    String newSid = createSid(session.getUri(), expiration);
    String newRefreshToken = createRefreshToken(session.getUri());

    return new SessionInfoData()
        .sid(newSid)
        .expiration(session.getExpiration())
        .locale(session.getLocale())
        .authentications(session.getAuthentications())
        .refreshToken(newRefreshToken);
  }

  private String createSid(URI sessionUri, OffsetDateTime expiration) {
    return tokenHandler.createToken(sessionUri.toString(), expiration);
  }

  private String createRefreshToken(URI sessionUri) {
    OffsetDateTime refreshExpiration = OffsetDateTime.now().plusMinutes(refreshTimeoutMins);
    return tokenHandler.createToken(REFRESHTOKEN_PREFIX + sessionUri.toString(), refreshExpiration);
  }

  private void registerSpringSecAuthToken(URI sessionUri) {
    Authentication authenticationToken = null;
    if (authenticationTokenProvider != null) {
      authenticationToken = authenticationTokenProvider.apply(sessionUri);
      Objects.requireNonNull(authenticationToken, "authenticationTokenProvider has returned null!");
      log.debug(
          "Authentication token has been created by the configured authenticationTokenProvider!");
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
  public Session readSession(URI sessionUri) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    Session session = currentSession.get();
    if (session != null && sessionUri.equals(session.getUri())) {
      return session;
    }
    try {
      session = storage.get().read(sessionUri, Session.class);
      currentSession.set(session);
      return session;
    } catch (Exception e) {
      log.error("Error reading session by uri", e);
      currentSession.remove();
      return null;
    }
  }

  private URI updateSession(URI sessionUri, UnaryOperator<Session> update) {
    URI uri = storage.get().update(sessionUri, Session.class, update);
    currentSession.remove();
    return uri;
  }

  @Override
  public Session initCurrentSession(URI sessionUri) {
    currentSession.remove();
    return readSession(sessionUri);
  }

  @Override
  public void setSession(URI sessionUri) {
    Session session = initCurrentSession(sessionUri);
    SessionAuthToken authToken = SessionAuthToken.create(session);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  @Override
  public void setSessionParameter(URI sessionUri, String key, String value) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    Objects.requireNonNull(key, "key can not be null!");
    updateSession(sessionUri, s -> s.putParametersItem(key, value));
  }

  @Override
  public String removeSessionParameter(URI sessionUri, String key) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    Objects.requireNonNull(key, "key can not be null!");

    String value = readSession(sessionUri).getParameters().get(key);

    if (value != null) {
      updateSession(sessionUri, s -> {
        Map<String, String> parameters = s.getParameters();
        if (parameters == null) {
          return s;
        }
        parameters.remove(key);
        return s;
      });
    }
    return value;
  }

  @Override
  public void setSessionLocale(URI sessionUri, String locale) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    updateSession(sessionUri, s -> s.locale(locale));
  }

  @Override
  public void setSessionExpiration(URI sessionUri, OffsetDateTime expiration) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    Objects.requireNonNull(expiration, "expiration can not be null!");
    updateSession(sessionUri, s -> s.expiration(expiration));
  }

  @Override
  public void addSessionAuthentication(URI sessionUri, AccountInfo accountInfo) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    Objects.requireNonNull(accountInfo, "accountInfo can not be null!");
    updateSession(sessionUri, s -> {
      List<AccountInfo> authentications = s.getAuthentications();
      if (authentications == null) {
        authentications = new ArrayList<>();
        s.setAuthentications(authentications);
      }
      authentications.removeIf(a -> accountInfo.getKind().equals(a.getKind()));
      authentications.add(accountInfo);
      return s;
    });
  }

  @Override
  public void removeSessionAuthentication(URI sessionUri, String kind) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    Objects.requireNonNull(kind, "kind can not be null!");
    updateSession(sessionUri, s -> {
      List<AccountInfo> authentications = s.getAuthentications();
      if (authentications == null) {
        return s;
      }
      authentications.removeIf(a -> kind.equals(a.getKind()));
      if (authentications.isEmpty()) {
        s.setUser(null);
      }

      return s;
    });
  }

  @Override
  public void setSessionUser(URI sessionUri, URI userUri) {
    Objects.requireNonNull(sessionUri, EXPMSG_MISSING_SESSIONURI);
    updateSession(sessionUri, s -> s.user(userUri));
  }

  @Override
  public void startTechnicalSession(URI technicalUserUri) {
    Assert.notNull(technicalUserUri, "technicalUserUri cannot be null");

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      log.warn("Technical session user is overwritten,"
          + "because an authentication was already present"
          + "in the security context!");
    }

    User technicalUser = orgApi.getUser(technicalUserUri);
    if (technicalUser == null) {
      throw new IllegalArgumentException("The given technical user does not exist!");
    }

    Session session = new Session();
    session.putParametersItem("sessionKind", TECHNICAL);
    session.setUser(technicalUserUri);
    AccountInfo accountInfo =
        SecurityContextUtility.getDefaultAccountInfoProvider(orgApi).apply(technicalUser, null);
    accountInfo.addRolesItem("ROLE_technical");
    accountInfo.setKind(TECHNICAL);
    session.addAuthenticationsItem(accountInfo);
    storage.get().saveAsNew(session);
    log.debug("Technical session saved!\n{}", session);

    SessionAuthToken authToken = SessionAuthToken.create(session);
    SecurityContextHolder.getContext().setAuthentication(authToken);

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

  @Override
  public void addViewContext(URI sessionUri, UUID viewContextUuid, URI viewContextUri) {
    log.debug("Adding viewContext (uuid={}) to session (uri={})", viewContextUuid, sessionUri);
    storage.get().update(sessionUri, Session.class,
        s -> s.putViewContextsItem(viewContextUuid.toString(), viewContextUri));

  }
}
