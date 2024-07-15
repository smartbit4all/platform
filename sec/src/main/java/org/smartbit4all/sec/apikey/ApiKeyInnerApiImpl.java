package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.core.object.ObjectNode;

public class ApiKeyInnerApiImpl extends ApiKeyImplementationBase implements ApiKeyInnerApi {

  private static final Logger log = LoggerFactory.getLogger(ApiKeyInnerApiImpl.class);

  @Override
  public Stream<ObjectNode> findApiKeysOfUser(URI userUri) {
    Objects.requireNonNull(userUri, "userUri can not be null!");

    StoredMap userScopedMap = getUserScopedMap(userUri);
    if (!userScopedMap.exists()) {
      return Stream.empty();
    }

    return userScopedMap.uris().values().stream().map(uri -> objectApi.loadLatest(uri));
  }

}
