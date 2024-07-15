package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.security.bean.ApiKey;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

public class ApiKeyApiImpl extends ApiKeyImplementationBase implements ApiKeyApi {

  private static final Logger log = LoggerFactory.getLogger(ApiKeyApiImpl.class);

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private PasswordEncoder passwordEncoder;


  /**
   * An ApiKey is created with an encoded token. Registered to a global StoredMap by the token, and
   * also registered to user scoped StoredMap.
   */
  @Override
  public ApiKey createApiKey(URI userUri, String token, OffsetDateTime expiration,
      List<String> scope) {
    Objects.requireNonNull(userUri, "userUri can not be null!");
    Objects.requireNonNull(token, "token can not be null!");
    if (token.isEmpty()) {
      throw new IllegalArgumentException("The api key token can not be null!");
    }
    if (expiration != null && expiration.isBefore(OffsetDateTime.now())) {
      throw new IllegalArgumentException(
          "ApiKey can not be created with expiration date already passed.");
    }

    UserActivityLog createActivityLog = createActivityLog();

    String encodedToken = passwordEncoder.encode(token);
    ApiKey apiKey = new ApiKey()
        .user(userUri)
        .expiration(expiration)
        .scope(scope == null ? Collections.emptyList() : scope)
        .created(createActivityLog)
        .token(encodedToken);

    URI apiKeyUri = objectApi.saveAsNew(SCHEMA, apiKey);
    apiKey.setUri(objectApi.getLatestUri(apiKeyUri));

    StoredMap globalMap = getGlobalMap();
    addToStoredMap(globalMap, apiKey);

    StoredMap userScopedMap = getUserScopedMap(userUri);
    addToStoredMap(userScopedMap, apiKey);

    return apiKey;
  }

  private UserActivityLog createActivityLog() {
    UserActivityLog createActivityLog = null;
    if (sessionApi == null) {
      log.warn("Unable to create activity log. The Sessionapi is missing!");
    } else {
      createActivityLog = sessionApi.createActivityLog();
    }
    return createActivityLog;
  }

  private void addToStoredMap(StoredMap storedMap, ApiKey apiKey) {
    storedMap.update(map -> {
      if (map == null) {
        map = new HashMap<String, URI>();
      }
      map.put(apiKey.getToken(), apiKey.getUri());
      return map;
    });
  }

  @Override
  public ApiKey findApiKeyWithToken(String token) {
    Objects.requireNonNull(token, "token can not be null!");
    if (token.isEmpty()) {
      throw new IllegalArgumentException("token can not be empty!");
    }

    StoredMap apiKeyMap = getGlobalMap();
    if (!apiKeyMap.exists()) {
      return null;
    }

    return apiKeyMap.uris().entrySet().stream()
        .filter(mapEntry -> passwordEncoder.matches(token, mapEntry.getKey()))
        .findFirst()
        .map(mapEntry -> objectApi.loadLatest(mapEntry.getValue()))
        .map(node -> node.getObject(ApiKey.class))
        .orElse(null);

  }

  @Override
  public void revokeApiKey(URI apiKeyUri) {
    Objects.requireNonNull(apiKeyUri, "apiKeyUri can not be null!");

    ObjectNode apiKeyNode = objectApi.loadLatest(apiKeyUri);
    apiKeyNode.setValue(createActivityLog(), ApiKey.REVOKED);
    objectApi.save(apiKeyNode);
  }

  @Override
  public ApiKeyCheckResult checkApiKey(ApiKey apiKey, String apiName) {
    Objects.requireNonNull(apiKey, "apiKey can not be null!");

    if (!ObjectUtils.isEmpty(apiKey.getRevoked())) {
      return ApiKeyCheckResult.REVOKED;
    }

    if (!ObjectUtils.isEmpty(apiKey.getExpiration())
        && apiKey.getExpiration().isBefore(OffsetDateTime.now())) {
      return ApiKeyCheckResult.EXPIRED;
    }

    if (!ObjectUtils.isEmpty(apiName) && !ObjectUtils.isEmpty(apiKey.getScope())
        && !apiKey.getScope().contains(apiName)) {
      return ApiKeyCheckResult.NOT_SCOPED;
    }

    return ApiKeyCheckResult.OK;
  }

}
