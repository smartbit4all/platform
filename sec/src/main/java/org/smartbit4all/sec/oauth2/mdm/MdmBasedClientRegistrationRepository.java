package org.smartbit4all.sec.oauth2.mdm;

import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionException;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
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
    return builder
        .clientId(properties.getClientId())
        .clientSecret(properties.getClientSecret())
        .clientName(properties.getClientName())
        .redirectUriTemplate(properties.getRedirectUri())
        .scope(properties.getScope().trim().split("\\s*,\\s*"))
        .authorizationGrantType(new AuthorizationGrantType(properties.getAuthorizationGrantType()))
        .userNameAttributeName(properties.getUserNameAttribute())
        .build();
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
