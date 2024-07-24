package org.smartbit4all.sec.apikey;

import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Base class of ApiKey implementation to offer the same functionalities to the {@link ApiKeyApi}
 * and the {@link ApiKeyInnerApi} implementations.
 */
public abstract class ApiKeyImplementationBase {

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected CollectionApi collectionApi;

  @Autowired
  protected MasterDataManagementApi mdmApi;

  @Autowired
  protected SessionApi sessionApi;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  protected final MDMEntryApi getApiKeyMdmEntryApi() {
    return mdmApi.getApi(
        MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION, PlatformApiConfig.API_KEYS);
  }

  protected final MDMEntryApi getApiKeyScopeMdmEntryApi() {
    return mdmApi.getApi(
        MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        PlatformApiConfig.API_KEY_SCOPES);
  }

}
