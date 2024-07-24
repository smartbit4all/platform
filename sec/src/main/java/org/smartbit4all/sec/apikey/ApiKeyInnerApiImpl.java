package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.security.bean.ApiKey;
import org.smartbit4all.core.object.ObjectNode;

public class ApiKeyInnerApiImpl extends ApiKeyImplementationBase implements ApiKeyInnerApi {

  private static final Logger log = LoggerFactory.getLogger(ApiKeyInnerApiImpl.class);

  @Override
  public Stream<ObjectNode> findApiKeysOfUser(URI userUri) {
    Objects.requireNonNull(userUri, "userUri can not be null!");

    return getApiKeyMdmEntryApi().getList().nodes()
        .filter(apiKeyNode -> objectApi
            .equalsIgnoreVersion(apiKeyNode.getValue(URI.class, ApiKey.USER), userUri));

  }

  @Override
  public ObjectNode createApiKey(URI userUri, String token, OffsetDateTime expiration,
      List<URI> scope) {
    Objects.requireNonNull(userUri, "userUri can not be null!");
    Objects.requireNonNull(token, "token can not be null!");
    if (token.isEmpty()) {
      throw new IllegalArgumentException("The api key token can not be null!");
    }
    if (expiration != null && expiration.isBefore(OffsetDateTime.now())) {
      throw new IllegalArgumentException(
          "ApiKey can not be created with expiration date already passed.");
    }

    String encodedToken = passwordEncoder.encode(token);
    ApiKey apiKey = new ApiKey()
        .user(objectApi.getLatestUri(userUri))
        .expiration(expiration)
        .scope(scope == null ? Collections.emptyList() : scope)
        .token(encodedToken);
    return objectApi.create(ApiKeyApi.SCHEMA, apiKey);
  }

}
