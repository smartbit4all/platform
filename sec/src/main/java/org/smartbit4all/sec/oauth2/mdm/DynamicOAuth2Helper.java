package org.smartbit4all.sec.oauth2.mdm;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;
import org.smartbit4all.core.object.ObjectApi;

public class DynamicOAuth2Helper {

  static final String DYNAMIC_OAUTH2_KIND_BASE = "oauth2-";

  private DynamicOAuth2Helper() {}

  static List<String> getOAuth2ClientRegIdsOfSession(SessionApi sessionApi) {
    try {
      return sessionApi.getAuthentications().stream()
          .filter(accInfo -> accInfo.getKind()
              .startsWith(DYNAMIC_OAUTH2_KIND_BASE))
          .map(accInfo -> accInfo.getKind()
              .substring(DYNAMIC_OAUTH2_KIND_BASE.length()))
          .collect(Collectors.toList());
    } catch (NoCurrentSessionException e) {
      return Collections.emptyList();
    }
  }

  static String getAuthInfoKind(String clientRegistrationId) {
    return DYNAMIC_OAUTH2_KIND_BASE + clientRegistrationId;
  }

  static final MDMEntryApi getClientPropertyMdmEntryApi(MasterDataManagementApi mdmApi) {
    return mdmApi.getApi(
        MdmBasedDynamicOAuthConfig.MDM_DEF,
        MdmBasedDynamicOAuthConfig.MDM_ENTRY);
  }

  static OAuthClientProperties getClientPropsForRegId(String regId, MasterDataManagementApi mdmApi,
      ObjectApi objectApi) {
    URI propUri = DynamicOAuth2Helper.getClientPropertyMdmEntryApi(mdmApi)
        .getUniqueMap(OAuthClientProperties.REGISTRATION_ID)
        .uris().get(regId);
    if (propUri == null) {
      return null;
    }

    return objectApi.load(propUri).getObject(OAuthClientProperties.class);
  }

}
