package org.smartbit4all.sec.oauth2.mdm;

import java.util.Map;

import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;

public class DynamicOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

  private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

  private final AntPathRequestMatcher authorizationRequestMatcher;
  
  private final ClientRegistrationRepository clientRegistrationRepository;
  
  private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;
  private final DefaultOAuth2AuthorizationRequestResolver defaultResolverWithPKCE;
  
  public DynamicOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
    Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
    this.clientRegistrationRepository = clientRegistrationRepository;

    String authorizationRequestBaseUri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
    this.authorizationRequestMatcher = new AntPathRequestMatcher(
        authorizationRequestBaseUri + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");

    defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
        authorizationRequestBaseUri);
    defaultResolverWithPKCE = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
        authorizationRequestBaseUri);
    defaultResolverWithPKCE.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers
        .withPkce());
  }
  
  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    String registrationId = resolveRegistrationId(request);
    if (registrationId == null) {
      return null;
    }
    return getWrappedResolver(registrationId).resolve(request);
  }
  
  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
    return getWrappedResolver(registrationId).resolve(request);
  }
  
  private OAuth2AuthorizationRequestResolver getWrappedResolver(String registrationId) {
    ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
    if (clientRegistration == null) {
      throw new IllegalArgumentException("Invalid Client Registration with Id: " + registrationId);
    }
    
    Map<String, Object> metadata = clientRegistration.getProviderDetails().getConfigurationMetadata();
    if(Boolean.TRUE.equals(metadata.get(OAuthClientProperties.IS_PKCE_ENABLED))) {
      return defaultResolverWithPKCE;
    } else {
      return defaultResolver;
    }
  }
  
  private String resolveRegistrationId(HttpServletRequest request) {
    if (this.authorizationRequestMatcher.matches(request)) {
      return this.authorizationRequestMatcher.matcher(request)
        .getVariables()
        .get(REGISTRATION_ID_URI_VARIABLE_NAME);
    }
    return null;
  }

}
