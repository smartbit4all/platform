package org.smartbit4all.sec.apikey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ApiKeyMaintainerConfig {

  @Autowired
  private ApiKeyApi apiKeyApi;

  // default 30mp initdelay and 2 hours fixeddelay
  @Scheduled(initialDelayString = "${api-key-maintainer.schedule.initdelay:30000}",
      fixedDelayString = "${api-key-maintainer.schedule.fixeddelay:7200000}")
  public void maintainExpiredApiKeys() {
    apiKeyApi.maintainExpiredApiKeys();
  }

}
