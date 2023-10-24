package org.smartbit4all.sec.utils;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smartbit4all.api.session.SessionApi;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.access.ExceptionTranslationFilter;

public class SecurityConfigUtils {

  public static final String SB4_SESSION_URI = "sb4SessionUri";

  private SecurityConfigUtils() {}

  public static void disableRedirectOnAccessDeniedForAnonymous(HttpSecurity http) throws Exception {
    http.exceptionHandling()
        .addObjectPostProcessor(new ObjectPostProcessor<ExceptionTranslationFilter>() {
          @Override
          public <O extends ExceptionTranslationFilter> O postProcess(O object) {
            object.setAuthenticationTrustResolver(new DisabledAuthenticationTrustResolver());
            return object;
          }
        });
  }

  private static class DisabledAuthenticationTrustResolver implements AuthenticationTrustResolver {

    @Override
    public boolean isAnonymous(Authentication authentication) {
      return false;
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
      return false;
    }

  }

  /*
   * the session uri could be stored in the authorization request by adding the below resolver in
   * the configuration like this:
   * http.oauth2Login().authorizationEndpoint().authorizationRequestResolver(
   * getOauth2AuthReqResolver())
   */
  public static DefaultOAuth2AuthorizationRequestResolver getOauth2AuthReqResolver(
      SessionApi sessionApi, ClientRegistrationRepository clientRegistrationRepository) {

    DefaultOAuth2AuthorizationRequestResolver resolver =
        new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
            OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    resolver.setAuthorizationRequestCustomizer(builder -> {
      String sessionUriTxt = null;
      try {
        sessionUriTxt = sessionApi.getSessionUri().toString();
      } catch (Exception e) {
        throw new IllegalStateException("There is no current session available", e);
      }
      final String sessionUri = sessionUriTxt;
      builder.additionalParameters(map -> map.put(SB4_SESSION_URI, sessionUri));
    });
    return resolver;
  }


  public static AuthorizationRequestRepository<OAuth2AuthorizationRequest> getOauth2AuthReqRepository(
      SessionApi sessionApi) {

    return new AuthorizationRequestRepository<OAuth2AuthorizationRequest>() {

      private HttpSessionOAuth2AuthorizationRequestRepository defaultRepo =
          new HttpSessionOAuth2AuthorizationRequestRepository();

      @Override
      public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return defaultRepo.loadAuthorizationRequest(request);
      }

      @Override
      public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
          HttpServletRequest request, HttpServletResponse response) {

        String state = authorizationRequest.getState();

        String sessionUriTxt = null;
        try {
          sessionUriTxt = sessionApi.getSessionUri().toString();
        } catch (Exception e) {
          throw new IllegalStateException("There is no current session available", e);
        }
        // FIXME get the existing map and add the new uri there if multiple oauth flow is present
        request.getSession().setAttribute(SB4_SESSION_URI,
            Collections.singletonMap(state, sessionUriTxt));

        defaultRepo.saveAuthorizationRequest(authorizationRequest, request, response);
      }

      @Override
      public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return defaultRepo.removeAuthorizationRequest(request);
      }
    };
  }
}
