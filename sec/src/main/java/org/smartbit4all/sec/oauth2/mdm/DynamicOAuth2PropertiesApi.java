package org.smartbit4all.sec.oauth2.mdm;

import java.util.List;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.security.bean.OAuthClientProperties;

public interface DynamicOAuth2PropertiesApi {

  static final String DYNAMIC_OAUTH2_KIND_BASE = "oauth2-";

  static String getAuthInfoKind(String clientRegistrationId) {
    return DYNAMIC_OAUTH2_KIND_BASE + clientRegistrationId;
  }

  OAuthClientProperties getClientPropsForRegId(String regId);

  MDMEntryApi getClientPropertyMdmEntryApi();

  List<String> getOAuth2ClientRegIdsOfSession();

}
