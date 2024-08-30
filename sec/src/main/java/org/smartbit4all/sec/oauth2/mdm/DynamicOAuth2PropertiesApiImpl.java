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
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOAuth2PropertiesApiImpl implements DynamicOAuth2PropertiesApi {

  @Autowired
  private MasterDataManagementApi mdmApi;
  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private SessionApi sessionApi;

  @Override
  public List<String> getOAuth2ClientRegIdsOfSession() {
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

  @Override
  public final MDMEntryApi getClientPropertyMdmEntryApi() {
    return mdmApi.getApi(
        MdmBasedDynamicOAuthConfig.MDM_DEF,
        MdmBasedDynamicOAuthConfig.MDM_ENTRY);
  }

  @Override
  public OAuthClientProperties getClientPropsForRegId(String regId) {
    URI propUri = getClientPropertyMdmEntryApi()
        .getUniqueMap(OAuthClientProperties.REGISTRATION_ID)
        .uris().get(regId);
    if (propUri == null) {
      return null;
    }

    return objectApi.load(propUri).getObject(OAuthClientProperties.class);
  }

}
