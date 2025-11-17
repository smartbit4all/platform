package org.smartbit4all.sec.oauth2.mdm;

import java.util.HashMap;
import java.util.Map;

import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionException;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.ObjectUtils;

public class MdmBasedClientRegistrationRepository implements ClientRegistrationRepository {

  @Autowired
  private DynamicOAuth2PropertiesApi dynamicOAuth2PropertiesApi;

  @Override
  public ClientRegistration findByRegistrationId(String registrationId) {
    OAuthClientProperties properties =
        dynamicOAuth2PropertiesApi.getClientPropsForRegId(registrationId);
    return clientRegistrationFromProperty(properties);
  }

  private ClientRegistration clientRegistrationFromProperty(OAuthClientProperties properties) {
    if (properties == null) {
      return null;
    }
    Builder builder = null;
    if (!ObjectUtils.isEmpty(properties.getIssuerUri())) {
      builder = ClientRegistrations.fromIssuerLocation(properties.getIssuerUri())
          .registrationId(properties.getRegistrationId());
    } else if (!ObjectUtils.isEmpty(properties.getCommonProvider())) {
      CommonOAuth2Provider commonProvider = getCommonProvider(properties.getCommonProvider());
      if (commonProvider == null) {
        throw new IllegalStateException("There is no common provider with name: "
            + properties.getCommonProvider() + "on mdm set up OAuth client registration with id: "
            + properties.getRegistrationId());
      }
      builder = commonProvider.getBuilder(properties.getRegistrationId());
    } else {
      builder = ClientRegistration.withRegistrationId(properties.getRegistrationId());
      builder.authorizationUri(properties.getAuthorizationUri())
          .tokenUri(properties.getTokenUri())
          .userInfoUri(properties.getUserInfoUri())
          .jwkSetUri(properties.getJwkSetUri());
    }
    
    Map<String, Object> metadata = new HashMap<String, Object>();
    metadata.put(OAuthClientProperties.IS_PKCE_ENABLED, properties.getIsPkceEnabled());
    
    return builder
        .clientId(properties.getClientId())
        .clientSecret(properties.getClientSecret())
        .clientName(properties.getClientName())
        .redirectUri(properties.getRedirectUri())
        .scope(properties.getScope().trim().split("\\s*,\\s*"))
        .authorizationGrantType(new AuthorizationGrantType(properties.getAuthorizationGrantType()))
        .userNameAttributeName(properties.getUserNameAttribute())
        .userInfoAuthenticationMethod(getUserInfoAuthenticationMethod(properties.getUserInfoAuthenticationMethod()))
        .clientAuthenticationMethod(new ClientAuthenticationMethod(properties.getClientAuthenticationMethod()))
        .providerConfigurationMetadata(metadata)
        .build();
  }

  private AuthenticationMethod getUserInfoAuthenticationMethod(String userInfoAuthenticationMethod) {
    if (ObjectUtils.isEmpty(userInfoAuthenticationMethod)) {
      return AuthenticationMethod.HEADER;
    }

    switch (userInfoAuthenticationMethod.toLowerCase()) {
    case "form":
      return AuthenticationMethod.FORM;
    case "query":
      return AuthenticationMethod.QUERY;
    case "header":
    default:
      return AuthenticationMethod.HEADER;
    }
  }

  private static CommonOAuth2Provider getCommonProvider(String providerId) {
    try {
      return ApplicationConversionService.getSharedInstance().convert(providerId,
          CommonOAuth2Provider.class);
    } catch (ConversionException ex) {
      return null;
    }
  }

}
