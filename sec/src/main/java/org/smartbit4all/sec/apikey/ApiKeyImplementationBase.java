package org.smartbit4all.sec.apikey;

import java.net.URI;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class of ApiKey implementation to offer the same functionalities to the {@link ApiKeyApi}
 * and the {@link ApiKeyInnerApi} implementations.
 */
public abstract class ApiKeyImplementationBase {

  protected static final String SCHEMA = "security-api-key";
  protected static final String SM_KEYSBYTOKEN = "apikeys-by-token";

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected CollectionApi collectionApi;

  protected final StoredMap getUserScopedMap(URI userUri) {
    return collectionApi.map(userUri, SCHEMA, SM_KEYSBYTOKEN);
  }

  protected final StoredMap getGlobalMap() {
    return collectionApi.map(SCHEMA, SM_KEYSBYTOKEN);
  }

}
