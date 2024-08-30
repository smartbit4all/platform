package org.smartbit4all.sec.oauth2.mdm;

import org.smartbit4all.api.config.PlatformSecurityOption;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.smartbit4all.sec.oauth2.OAuth2SessionAuthSuccessHandler;
import org.smartbit4all.sec.oauth2.OAuth2SessionAuthSuccessHandler.OrgUserHandler;
import org.smartbit4all.sec.oauth2.SessionOAuth2AuthorizedClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@Configuration
public class MdmBasedDynamicOAuthConfig {

  static final String MDM_DEF = MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION;
  static final String MDM_ENTRY = "OAuthClientProperties";

  @Value("${oauth.successfull.redirect-path:/}")
  private String successfullRedirectPath;

  @Bean
  ClientRegistrationRepository mdmBasedClientRegistrationRepository() {
    return new MdmBasedClientRegistrationRepository();
  }

  @Bean
  DynamicOAuth2AuthenticationDataProvider dynamicOAuth2AuthenticationDataProvider() {
    return new DynamicOAuth2AuthenticationDataProvider();
  }


  @Bean
  public OrgUserHandler dynamicOAuthOrgUserHandler() {
    return new DynamicOAuthOrgUserHandler();
  }

  @Bean
  public OAuth2SessionAuthSuccessHandler oauth2SessionAuthSuccessHandler() {
    OAuth2SessionAuthSuccessHandler oAuth2SessionAuthSuccessHandler =
        new OAuth2SessionAuthSuccessHandler(successfullRedirectPath);
    oAuth2SessionAuthSuccessHandler.setCreateMissingUser(true);
    oAuth2SessionAuthSuccessHandler.setOrgUserHandler(dynamicOAuthOrgUserHandler());

    return oAuth2SessionAuthSuccessHandler;
  }

  @Bean
  public DynamicOAuth2LogoutHandler dynamicOauth2LogoutHandler() {
    return new DynamicOAuth2LogoutHandler();
  }

  @Bean
  public DynamicOidcClientLogoutHandler dynamicOidcClientLogoutHandler() {
    return new DynamicOidcClientLogoutHandler();
  }

  @Bean
  public DynamicOAuth2AccessRequestFilter dynamicOauth2AccessRequestFilter() {
    return new DynamicOAuth2AccessRequestFilter();
  }

  @Bean
  public OAuth2AuthorizedClientManager oauth2AuthorizedClientManager(
      OAuth2AuthorizedClientRepository authorizedClientRepository) {
    return new DefaultOAuth2AuthorizedClientManager(mdmBasedClientRegistrationRepository(),
        authorizedClientRepository);
  }

  @Bean
  public OAuth2AuthorizedClientService oauth2AuthorizedClientService() {
    return new SessionOAuth2AuthorizedClientService(mdmBasedClientRegistrationRepository());
  }

  @Bean
  MDMDefinitionOption oauth2MdmOption() {
    MDMDefinition mdmDefinition =
        new MDMDefinition().name(MdmBasedDynamicOAuthConfig.MDM_DEF)
            .adminGroupName(PlatformSecurityOption.admin.getName());
    MDMDefinitionOption result = new MDMDefinitionOption(mdmDefinition);

    MDMEntryDescriptor entry = new MDMEntryDescriptor()
        .schema(MasterDataManagementApi.SCHEMA)
        .publishedListName(MdmBasedDynamicOAuthConfig.MDM_ENTRY)
        .name(MdmBasedDynamicOAuthConfig.MDM_ENTRY)
        .addConstraintsItem(new MDMEntryConstraint()
            .kind(KindEnum.UNIQUECASEINSENSITIVE)
            .addPathItem(OAuthClientProperties.REGISTRATION_ID))
        .editorViewName(MDMConstants.MDM_EDIT)
        .displayNameList(new LangString().defaultValue("OAuth Client Registrations")
            .putValueByLocaleItem("hu", "OAuth Kliens regisztrációk")
            .putValueByLocaleItem("en", "OAuth Client Registrations"))
        .displayNameForm(new LangString().defaultValue("OAuth Client Registration")
            .putValueByLocaleItem("hu", "OAuth Kliens regisztráció")
            .putValueByLocaleItem("en", "OAuth Client Registration"))
        .typeQualifiedName(OAuthClientProperties.class.getName())
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.CLIENT_ID)
                .addPathItem(OAuthClientProperties.CLIENT_ID))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.REGISTRATION_ID)
                .addPathItem(OAuthClientProperties.REGISTRATION_ID))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.SCOPE)
                .addPathItem(OAuthClientProperties.SCOPE))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.AUTHORIZATION_URI)
                .addPathItem(OAuthClientProperties.AUTHORIZATION_URI))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.REDIRECT_URI)
                .addPathItem(OAuthClientProperties.REDIRECT_URI))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.TOKEN_URI)
                .addPathItem(OAuthClientProperties.TOKEN_URI))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.USER_INFO_URI)
                .addPathItem(OAuthClientProperties.USER_INFO_URI))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.JWK_SET_URI)
                .addPathItem(OAuthClientProperties.JWK_SET_URI))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.ISSUER_URI)
                .addPathItem(OAuthClientProperties.ISSUER_URI))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.CLIENT_NAME)
                .addPathItem(OAuthClientProperties.CLIENT_NAME))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name(OAuthClientProperties.AUTHORIZATION_GRANT_TYPE)
                .addPathItem(OAuthClientProperties.AUTHORIZATION_GRANT_TYPE));
    result.addDescriptor(entry);

    return result;
  }
}
