package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.security.bean.ApiKey;
import org.smartbit4all.api.security.bean.ApiKeyScope;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class ApiKeyApiImpl extends ApiKeyImplementationBase implements ApiKeyApi {

  private static final Logger log = LoggerFactory.getLogger(ApiKeyApiImpl.class);

  @Autowired
  private ApiKeyInnerApi innerApi;
  @Autowired
  private ValueSetApi valueSetApi;

  /**
   * An ApiKey is created with an encoded token. Saved with MDM.
   */
  @Override
  public ApiKey addApiKey(URI userUri, String token, OffsetDateTime expiration,
      List<URI> scope) {
    ObjectNode apiKeyNode = innerApi.createApiKey(userUri, token, expiration, scope);

    URI apiKeyUri = getApiKeyMdmEntryApi().save(apiKeyNode).get(0);
    apiKeyNode.setValue(apiKeyUri, ApiKey.URI);

    return apiKeyNode.getObject(ApiKey.class);
  }

  @Override
  public ApiKey findApiKeyWithToken(String token) {
    Objects.requireNonNull(token, "token can not be null!");
    if (token.isEmpty()) {
      throw new IllegalArgumentException("token can not be empty!");
    }

    StoredMap apiKeyMap = getApiKeyMdmEntryApi().getUniqueMap(ApiKey.TOKEN);
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

    getApiKeyMdmEntryApi().remove(apiKeyUri);
  }

  @Override
  public ApiKeyCheckResult checkApiKey(ApiKey apiKey, List<URI> scopeUris) {
    if (apiKey == null) {
      return ApiKeyCheckResult.NO_MATCH;
    }

    if (!ObjectUtils.isEmpty(apiKey.getRevoked())) {
      return ApiKeyCheckResult.REVOKED;
    }

    if (!ObjectUtils.isEmpty(apiKey.getExpiration())
        && apiKey.getExpiration().isBefore(OffsetDateTime.now())) {
      return ApiKeyCheckResult.EXPIRED;
    }

    if (ObjectUtils.isEmpty(scopeUris) || !ObjectUtils.isEmpty(apiKey.getScope())
        && apiKey.getScope().stream()
            .noneMatch(apiKeyScopeUri -> scopeUris.stream()
                .anyMatch(scopeUri -> objectApi.equalsIgnoreVersion(apiKeyScopeUri, scopeUri)))) {
      return ApiKeyCheckResult.NOT_SCOPED;
    }

    return ApiKeyCheckResult.OK;
  }

  public void maintainExpiredApiKeys() {
    OffsetDateTime now = OffsetDateTime.now();
    getApiKeyMdmEntryApi().getList().nodes()
        .filter(apiKeyNode -> {
          OffsetDateTime expiration = apiKeyNode.getValue(OffsetDateTime.class, ApiKey.EXPIRATION);
          return expiration != null && expiration.isBefore(now);
        })
        .map(ObjectNode::getObjectUri)
        .forEach(apiKeyUriToRevoke -> getApiKeyMdmEntryApi().remove(apiKeyUriToRevoke));
  }

  @Override
  public URI createApiKeyScope(ApiKeyScope apiKeyScope) {
    Objects.requireNonNull(apiKeyScope, "apiKeyScope can not be null!");
    Objects.requireNonNull(apiKeyScope.getName(), "apiKeyScope.getName() can not be null!");
    Objects.requireNonNull(apiKeyScope.getPathPattern(),
        "apiKeyScope.getPathPattern() can not be null!");

    if (apiKeyScope.getUri() != null) {
      throw new IllegalArgumentException("The given ApiKeyScope bean already has an uri!");
    }
    ObjectNode apiKeyScopeNode = objectApi.create(SCHEMA, apiKeyScope);
    List<URI> mdmResult = getApiKeyScopeMdmEntryApi().save(apiKeyScopeNode);
    if (ObjectUtils.isEmpty(mdmResult) || mdmResult.size() != 1) {
      throw new IllegalStateException("Mdm save resulted with error on new ApiKeyScope object!");
    }

    if (valueSetApi.getDefinitionData(ApiKeyConstants.APIKEY_SCOPE) == null) {
      ValueSetDefinitionData userValueSetDefinitionData =
          new ValueSetDefinitionData()
              .kind(ValueSetDefinitionKind.LIST)
              .storageSchema(MasterDataManagementApi.SCHEMA)
              .containerName(ApiKeyConfig.API_KEY_SCOPES)
              .typeClass(ApiKeyScope.class.getName())
              .qualifiedName(ApiKeyConstants.APIKEY_SCOPE);
      valueSetApi.save(SCHEMA, userValueSetDefinitionData);
    }

    return mdmResult.get(0);
  }

  @Override
  public List<URI> updateApiKeyScope(String oldName, String newName, String newPath) {
    Objects.requireNonNull(oldName, "oldName can not be null!");
    Objects.requireNonNull(newName, "newName can not be null!");
    Objects.requireNonNull(newPath, "newPath can not be null!");

    return getApiKeyScopeMdmEntryApi().getList().nodes()
        .filter(scopeNode -> oldName.equals(scopeNode.getValueAsString(ApiKeyScope.NAME)))
        .map(scopeNode -> scopeNode
            .setValue(newName, ApiKeyScope.NAME)
            .setValue(newPath, ApiKeyScope.PATH_PATTERN))
        .map(objectApi::save)
        .collect(Collectors.toList());
  }

  @Override
  public List<URI> getMatchingApiKeyScopes(HttpServletRequest httpRequest) {
    return getApiKeyScopeMdmEntryApi().getList().nodes()
        .filter(scopeNode -> {
          String pathPattern = scopeNode.getValueAsString(ApiKeyScope.PATH_PATTERN);
          if (!ObjectUtils.isEmpty(pathPattern)
              && httpRequest.getRequestURI().matches(pathPattern)) {
            return true;
          }
          return false;
        })
        .map(ObjectNode::getObjectUri)
        .collect(Collectors.toList());
  }

  @Override
  public URI getApiKeyScopeByName(String name) {
    return getApiKeyScopeMdmEntryApi().getList().nodes()
        .filter(scopeNode -> Objects.equals(name, scopeNode.getValueAsString(ApiKeyScope.NAME)))
        .findAny()
        .map(ObjectNode::getObjectUri)
        .orElse(null);
  }

}
