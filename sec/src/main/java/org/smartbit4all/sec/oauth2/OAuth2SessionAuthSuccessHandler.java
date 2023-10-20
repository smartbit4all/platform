package org.smartbit4all.sec.oauth2;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authentication.DefaultAuthTokenProvider;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.smartbit4all.sec.token.SessionTokenHandler;
import org.smartbit4all.sec.utils.SecurityConfigUtils;
import org.smartbit4all.sec.utils.SecurityContextUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * On OAuth2 Authorization success:
 * <ul>
 * <li>an {@link AccountInfo} is set to the session.</li>
 * <li>a user uri is set in the session if it was not already</li>
 * <li>custom Authentication token is set to the security context (replacing the one set by the
 * spring oath2 handling)</li>
 * <li>OAuth2AuthorizedClient is saved with the new authentication token</li>
 * </ul>
 */
public class OAuth2SessionAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final Logger log = LoggerFactory.getLogger(OAuth2SessionAuthSuccessHandler.class);

  protected static final String REDIRECT_PARAMETER = "redirectParameterKey";

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @Autowired
  private SessionTokenHandler sessionTokenHandler;

  @Autowired
  private OrgApi orgApi;

  private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
      new HttpSessionOAuth2AuthorizationRequestRepository();

  private MissingUserHandler onMissingUser;

  private final List<SessionBasedAuthTokenProvider> authTokenProviders;

  private boolean createMissingUser = false;
  private String authorizationErrorPath = "/authorization/error";

  public static interface MissingUserHandler {
    URI onMissingUser(OAuth2AuthenticationToken oauthToken) throws Exception;
  }

  public OAuth2SessionAuthSuccessHandler(String targetUrl) {
    super(targetUrl);
    this.authTokenProviders = Arrays.asList(new DefaultAuthTokenProvider());
    setTargetUrlParameter(REDIRECT_PARAMETER);
  }

  public OAuth2SessionAuthSuccessHandler(String targetUrl,
      SessionBasedAuthTokenProvider... sessionAuthTokenProviders) {
    super(targetUrl);
    Assert.notEmpty(sessionAuthTokenProviders, "authTokenProvider cannot be empty");
    this.authTokenProviders = Arrays.asList(sessionAuthTokenProviders);
    setTargetUrlParameter(REDIRECT_PARAMETER);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    if (authentication instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

      // looking for sb4SessionToken in the request (header[Authentication bearer token] or in
      // cookies)
      String tokenFromRequest = sessionTokenHandler.getTokenFromRequest(request);
      String sessionUriTxt =
          tokenFromRequest == null ? null : sessionTokenHandler.getSubject(tokenFromRequest);

      // looking for sb4SessionToken in stored OAuth2AuthorizationRequest
      if (ObjectUtils.isEmpty(sessionUriTxt) && authorizationRequestRepository != null) {

        String httpSessionStateParam = request.getParameter(OAuth2ParameterNames.STATE);
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null && httpSessionStateParam != null) {
          Map<String, String> stateMap =
              (Map<String, String>) httpSession.getAttribute(SecurityConfigUtils.SB4_SESSION_URI);
          if (!ObjectUtils.isEmpty(stateMap)) {
            sessionUriTxt = stateMap.get(httpSessionStateParam);
          }
        }

        // OAuth2AuthorizationRequest authorizationRequest =
        // authorizationRequestRepository.loadAuthorizationRequest(request);
        // sessionUriTxt = (String) authorizationRequest.getAdditionalParameters()
        // .get(SecurityConfigUtils.SB4_SESSION_URI);
      }


      if (!ObjectUtils.isEmpty(sessionUriTxt)) {
        URI sessionURI = URI.create(sessionUriTxt);

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient authorizedClient =
            authorizedClientRepository.loadAuthorizedClient(registrationId, oauthToken, request);

        AccountInfo accountInfo = createDefaultAccountInfo(authorizedClient, oauthToken);
        sessionManagementApi.addSessionAuthentication(sessionURI, accountInfo);

        try {
          setSessionUser(sessionURI, oauthToken);
        } catch (Exception e) {
          log.debug("Unable to set session user.", e);
          handleAuthorizationFailure(request, response, oauthToken);
          clearAuthenticationAttributes(request);
          return;
        }

        AbstractAuthenticationToken sessionAuthToken =
            SecurityContextUtility.setSessionAuthenticationTokenInContext(request, sessionUriTxt,
                authTokenProviders,
                null, sessionManagementApi, log);
        authorizedClientRepository.saveAuthorizedClient(authorizedClient, sessionAuthToken, request,
            response);
      }
    }

    response.setHeader("Location", getDefaultTargetUrl());
    response.setStatus(302);

    super.onAuthenticationSuccess(request, response, authentication);
  }

  protected void handleAuthorizationFailure(HttpServletRequest request,
      HttpServletResponse response, OAuth2AuthenticationToken oauthToken) {
    response.setHeader("Location", authorizationErrorPath);
    response.setStatus(302);
  }

  private void setSessionUser(URI sessionURI, OAuth2AuthenticationToken oauthToken)
      throws Exception {
    String name = oauthToken.getName();
    Session session = sessionManagementApi.readSession(sessionURI);
    if (session.getUser() == null) {
      User user = orgApi.getUserByUsername(name);
      URI userUri = null;
      if (user == null) {
        if (onMissingUser != null) {
          userUri = onMissingUser.onMissingUser(oauthToken);
        } else {
          if (createMissingUser) {
            log.warn(
                "A user logined with sso but there was no mathcing local User object. Creating a new one...");
            userUri = orgApi.saveUser(new User()
                .username(name));
          }
        }
      } else {
        checkUser(user);
        userUri = user.getUri();
      }
      if (userUri != null) {
        sessionManagementApi.setSessionUser(sessionURI, userUri);
      } else {
        throw new Exception(
            "There was no user found or has no rights to log in with the authenticated ouath2 token!");
      }
    }
  }

  /**
   * Throws exception if the user can not be logged in
   */
  protected void checkUser(User user) throws Exception {
    // nope
    // override this to check user. e.g.: check permissions
  }

  private AccountInfo createDefaultAccountInfo(OAuth2AuthorizedClient authorizedClient,
      OAuth2AuthenticationToken oauthToken) {
    String registrationId = oauthToken.getAuthorizedClientRegistrationId();
    String name = oauthToken.getName();
    List<String> roles = oauthToken.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    AccountInfo accountInfo = new AccountInfo()
        .kind("oauth2-" + registrationId)
        .userName(name)
        .displayName(name)
        .roles(roles)
        .putParametersItem("registrationId", registrationId)
        .putParametersItem("expiresAt",
            "" + authorizedClient.getAccessToken().getExpiresAt().toEpochMilli());
    return accountInfo;
  }

  public void setOnMissingUser(MissingUserHandler onMissingUser) {
    Assert.notNull(onMissingUser, "onMissingUser cannot be null");
    this.onMissingUser = onMissingUser;
  }

  public void setAuthorizationRequestRepository(
      AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
    this.authorizationRequestRepository = authorizationRequestRepository;
  }

  public boolean isCreateMissingUser() {
    return createMissingUser;
  }

  public void setCreateMissingUser(boolean createMissingUser) {
    this.createMissingUser = createMissingUser;
  }

  public String getAuthorizationErrorPath() {
    return authorizationErrorPath;
  }

  public void setAuthorizationErrorPath(String authorizationErrorPath) {
    this.authorizationErrorPath = authorizationErrorPath;
  }

}
