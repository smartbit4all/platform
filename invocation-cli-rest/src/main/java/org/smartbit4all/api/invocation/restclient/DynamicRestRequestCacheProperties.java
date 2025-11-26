package org.smartbit4all.api.invocation.restclient;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.request.cache")
public class DynamicRestRequestCacheProperties {

  private boolean enabled = false;
  private int ttlMinutes = 5;
  private List<String> hostPatterns = List.of(".*");

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getTtlMinutes() {
    return ttlMinutes;
  }

  public void setTtlMinutes(int ttlMinutes) {
    this.ttlMinutes = ttlMinutes;
  }

  public List<String> getHostPatterns() {
    return hostPatterns;
  }

  public void setHostPatterns(List<String> hostPatterns) {
    this.hostPatterns = hostPatterns;
  }


}
