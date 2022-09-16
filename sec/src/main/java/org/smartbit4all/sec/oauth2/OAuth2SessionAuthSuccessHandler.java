package org.smartbit4all.sec.oauth2;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.smartbit4all.sec.utils.SecurityContextUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.Assert;

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

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @Autowired
  private SessionTokenHandler sessionTokenHandler;

  @Autowired
  private OrgApi orgApi;

  private Function<OAuth2AuthenticationToken, URI> onMissingUser;

  private final List<SessionBasedAuthTokenProvider> authTokenProviders;

  public OAuth2SessionAuthSuccessHandler(String targetUrl) {
    super(targetUrl);
    this.authTokenProviders = Arrays.asList(new DefaultAuthTokenProvider());
  }

  public OAuth2SessionAuthSuccessHandler(String targetUrl,
      SessionBasedAuthTokenProvider... sessionAuthTokenProviders) {
    super(targetUrl);
    Assert.notEmpty(sessionAuthTokenProviders, "authTokenProvider cannot be empty");
    this.authTokenProviders = Arrays.asList(sessionAuthTokenProviders);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    if (authentication instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

      String tokenFromRequest = sessionTokenHandler.getTokenFromRequest(request);
      String sessionUriTxt =
          tokenFromRequest == null ? null : sessionTokenHandler.getSubject(tokenFromRequest);
      if (sessionUriTxt != null) {
        URI sessionURI = URI.create(sessionUriTxt);

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient authorizedClient =
            authorizedClientRepository.loadAuthorizedClient(registrationId, oauthToken, request);

        AccountInfo accountInfo = createDefaultAccountInfo(authorizedClient, oauthToken);
        sessionManagementApi.addSessionAuthentication(sessionURI, accountInfo);

        setSessionUser(sessionURI, oauthToken);

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

  private void setSessionUser(URI sessionURI, OAuth2AuthenticationToken oauthToken) {
    String name = oauthToken.getName();
    Session session = sessionManagementApi.readSession(sessionURI);
    if (session.getUser() == null) {
      User user = orgApi.getUserByUsername(name);
      URI userUri = null;
      if (user == null) {
        if (onMissingUser != null) {
          userUri = onMissingUser.apply(oauthToken);
        } else {
          log.warn(
              "A user logined with sso but there was no mathcing local User object. Creating a new one...");
          userUri = orgApi.saveUser(new User()
              .username(name));
        }
      } else {
        userUri = user.getUri();
      }
      sessionManagementApi.setSessionUser(sessionURI, userUri);
    }
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

  public void setOnMissingUser(Function<OAuth2AuthenticationToken, URI> onMissingUser) {
    Assert.notNull(onMissingUser, "onMissingUser cannot be null");
    this.onMissingUser = onMissingUser;
  }

}
