package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.smartbit4all.api.security.bean.ApiKey;
import org.smartbit4all.api.security.bean.ApiKeyScope;

public interface ApiKeyApi {

  public static final String SCHEMA = "security-api-key";

  ApiKey addApiKey(URI userUri, String token, OffsetDateTime expiration, List<URI> scope);

  ApiKey findApiKeyWithToken(String token);

  void revokeApiKey(URI apiKeyUri);

  ApiKeyCheckResult checkApiKey(ApiKey apiKey, List<URI> scopeUris);

  void maintainExpiredApiKeys();

  URI createApiKeyScope(ApiKeyScope apiKeyScope);

  URI getApiKeyScopeByName(String name);

  void deleteApiKeyScope(String name);

  List<URI> getMatchingApiKeyScopes(HttpServletRequest httpRequest);

  List<URI> updateApiKeyScope(String oldName, String newName, String newPath);

  public enum ApiKeyCheckResult {
    //@formatter:off
    OK("The ApiKey is valid."),
    NO_MATCH("Tha ApiKey has no match."),
    EXPIRED("The ApiKey has been expired."),
    REVOKED("The ApiKey has been revoked."),
    NOT_SCOPED("The ApiKey has no scope for the given api.");
    //@formatter:on    
    String details;

    ApiKeyCheckResult(String details) {
      this.details = details;
    }
  }

}
