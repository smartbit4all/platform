package org.smartbit4all.sec.oauth2.mdm;

import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.sec.authentication.AuthenticationDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.util.ObjectUtils;

public class DynamicOAuth2AuthenticationDataProvider implements AuthenticationDataProvider {

  private static final String AUTHORIZATION_REQUEST_PATH = "authorizationRequestPath";

  @Autowired
  MasterDataManagementApi mdmApi;

  @Override
  public boolean supports(Session session) {
    List<AccountInfo> authentications = session.getAuthentications();
    if (ObjectUtils.isEmpty(authentications)) {
      return true;
    }

    // supports when no dynamic authentication yet.
    return authentications.stream()
        .noneMatch(a -> a.getKind() != null
            && a.getKind().startsWith(DynamicOAuth2Helper.DYNAMIC_OAUTH2_KIND_BASE));
  }

  @Override
  public List<AuthenticationProviderData> getProviderDataList(Session session) {
    return DynamicOAuth2Helper.getClientPropertyMdmEntryApi(mdmApi).getList().nodesFromCache()
        .map(propertyNode -> new AuthenticationProviderData()
            .kind(DynamicOAuth2Helper
                .getAuthInfoKind(propertyNode.getValueAsString(OAuthClientProperties.CLIENT_ID)))
            .putParametersItem(AUTHORIZATION_REQUEST_PATH,
                getAuthRequestPath(propertyNode))
            .putParametersItem(OAuthClientProperties.LABEL,
                propertyNode.getValueAsString(OAuthClientProperties.LABEL))
            .putParametersItem(OAuthClientProperties.LOGO,
                propertyNode.getValueAsString(OAuthClientProperties.LOGO)))
        .collect(Collectors.toList());
  }

  private String getAuthRequestPath(ObjectNode propertyNode) {
    return OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/"
        + propertyNode.getValueAsString(OAuthClientProperties.REGISTRATION_ID);
  }

}
