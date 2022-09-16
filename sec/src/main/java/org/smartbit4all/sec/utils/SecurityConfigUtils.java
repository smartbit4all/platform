package org.smartbit4all.sec.utils;

import java.net.URI;
import org.smartbit4all.api.session.SessionApi;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.access.ExceptionTranslationFilter;

public class SecurityConfigUtils {

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
      URI sessionUri = URI.create("no_session");
      try {
        sessionUri = sessionApi.getSessionUri();
      } catch (Exception e) {
        // TODO: add proper validation
      }
      String sessionUriTxt = sessionUri.toString();
      builder.additionalParameters(map -> map.put("sessionUri", sessionUriTxt));
    });
    return resolver;
  }
}
