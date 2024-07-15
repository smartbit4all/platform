package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.util.stream.Stream;
import org.smartbit4all.core.object.ObjectNode;

public interface ApiKeyInnerApi {

  Stream<ObjectNode> findApiKeysOfUser(URI userUri);

}
