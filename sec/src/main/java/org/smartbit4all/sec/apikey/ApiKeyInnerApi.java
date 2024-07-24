package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.core.object.ObjectNode;

public interface ApiKeyInnerApi {

  ObjectNode createApiKey(URI userUri, String token, OffsetDateTime expiration,
      List<URI> scope);

  Stream<ObjectNode> findApiKeysOfUser(URI userUri);

}
