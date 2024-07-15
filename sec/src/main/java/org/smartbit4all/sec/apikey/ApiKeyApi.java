package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.smartbit4all.api.security.bean.ApiKey;

public interface ApiKeyApi {

  ApiKey createApiKey(URI userUri, String token, OffsetDateTime expiration, List<String> scope);

  ApiKey findApiKeyWithToken(String token);

  void revokeApiKey(URI apiKeyUri);

  ApiKeyCheckResult checkApiKey(ApiKey apiKey, String apiName);

  public enum ApiKeyCheckResult {
    //@formatter:off
    OK("The ApiKey is valid."), 
    EXPIRED("The ApiKey has been expired."),
    REVOKED("The ApiKey has been revoked."),
    NOT_SCOPED("The ApiKey has no skope for the given api.");
    //@formatter:on    
    String details;

    ApiKeyCheckResult(String details) {
      this.details = details;
    }
  }

}
