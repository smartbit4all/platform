package org.smartbit4all.api.session.restserver.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.GetAuthenticationProvidersResponse;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.bean.SessionInfoData;
import org.smartbit4all.api.session.restserver.SessionApiDelegate;
import org.smartbit4all.sec.authentication.AuthenticationDataProvider;
import org.smartbit4all.sec.token.SessionTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

public class SessionApiDelegateImpl implements SessionApiDelegate {

  private static final Logger log = LoggerFactory.getLogger(SessionApiDelegateImpl.class);

  @Autowired(required = false) // TODO remove 'required=false'
  private SessionApi sessionApi;

  @Autowired(required = false) // TODO remove 'required=false'
  private SessionTokenHandler tokenHandler;

  @Autowired
  private HttpServletRequest request;

  @Autowired(required = false) // TODO remove 'required=false'
  private List<AuthenticationDataProvider> authenticationDataProviders;

  @Override
  public ResponseEntity<SessionInfoData> startSession() throws Exception {

    SessionInfoData sessionInfoData = sessionApi.startSession();

    return ResponseEntity.ok(sessionInfoData);
  }

  @Override
  public ResponseEntity<SessionInfoData> getSession() throws Exception {
    String token = tokenHandler.getTokenFromRequest(request);
    if (ObjectUtils.isEmpty(token) || "null".equals(token)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    Session currentSession = sessionApi.currentSession();
    if (currentSession == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    String sid = tokenHandler.getTokenFromRequest(request);

    return ResponseEntity.ok(new SessionInfoData()
        .sid(sid)
        .expiration(currentSession.getExpiration())
        .locale(currentSession.getLocale())
        .authentications(currentSession.getAuthentications()));
  }

  @Override
  public ResponseEntity<GetAuthenticationProvidersResponse> getAuthenticationProviders()
      throws Exception {
    String token = tokenHandler.getTokenFromRequest(request);
    if (ObjectUtils.isEmpty(token) || "null".equals(token)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    Session session = sessionApi.currentSession();

    if (session == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    List<AuthenticationProviderData> poviderData = authenticationDataProviders.stream()
        .filter(p -> p.supports(session))
        .map(p -> p.getProviderData(session))
        .collect(Collectors.toList());

    if (ObjectUtils.isEmpty(poviderData)) {
      log.debug("There is no AuthenticationDataProvider for the given session!");
    }

    return ResponseEntity.ok(new GetAuthenticationProvidersResponse()
        .authenticationProviders(poviderData));
  }


}
