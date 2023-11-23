package org.smartbit4all.sec.oauth2;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

  public static final String AI_PARAM_ID_TOKEN = "idToken";
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
      if (ObjectUtils.isEmpty(sessionUriTxt)/* && authorizationRequestRepository != null */) {

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


        User user = null;
        try {
          user = setSessionUser(sessionURI, oauthToken);
        } catch (Exception e) {
          log.debug("Unable to set session user.", e);
          handleAuthorizationFailure(request, response, oauthToken);
          clearAuthenticationAttributes(request);
          return;
        }
        AccountInfo accountInfo = createDefaultAccountInfo(user, authorizedClient, oauthToken);
        sessionManagementApi.addSessionAuthentication(sessionURI, accountInfo);

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

  private User setSessionUser(URI sessionURI, OAuth2AuthenticationToken oauthToken)
      throws Exception {
    String name = oauthToken.getName();
    Session session = sessionManagementApi.readSession(sessionURI);
    User user = null;
    if (session.getUser() == null) {
      user = orgApi.getUserByUsername(name);
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
    return user;
  }

  /**
   * Throws exception if the user can not be logged in
   */
  protected void checkUser(User user) throws Exception {
    // nope
    // override this to check user. e.g.: check permissions
  }

  private AccountInfo createDefaultAccountInfo(User user, OAuth2AuthorizedClient authorizedClient,
      OAuth2AuthenticationToken oauthToken) {
    String registrationId = oauthToken.getAuthorizedClientRegistrationId();
    List<String> oauthRoles =
        oauthToken.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    AccountInfo accountInfo =
        SecurityContextUtility.getDefaultAccountInfoProvider(orgApi).apply(user, oauthToken);
    accountInfo.setKind("oauth2-" + registrationId);
    accountInfo.putParametersItem("registrationId", registrationId);
    accountInfo.putParametersItem("expiresAt",
        "" + authorizedClient.getAccessToken().getExpiresAt().toEpochMilli());
    if (oauthToken.getPrincipal() instanceof OidcUser) {
      // FIXME it might be not so good to store here... but for now there is no better option
      accountInfo.putParametersItem(AI_PARAM_ID_TOKEN,
          ((OidcUser) oauthToken.getPrincipal()).getIdToken().getTokenValue());
    }
    List<String> roles = accountInfo.getRoles();
    if (roles == null) {
      roles = new ArrayList<>();
      accountInfo.setRoles(roles);
    }
    roles.addAll(oauthRoles);
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
