package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.util.function.Supplier;
import org.smartbit4all.api.session.SessionManagementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ApiKeyMaintainerConfig {

  public static final String API_KEY_MAINTAINER_TECH_USER_PROVIDER =
      "apiKeyMaintainerTechnicalUserProvider";

  @Autowired
  private ApiKeyApi apiKeyApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  @Qualifier(API_KEY_MAINTAINER_TECH_USER_PROVIDER)
  private Supplier<URI> technicalUserProvider;

  // default 30mp initdelay and 2 hours fixeddelay
  @Scheduled(initialDelayString = "${api-key-maintainer.schedule.initdelay:30000}",
      fixedDelayString = "${api-key-maintainer.schedule.fixeddelay:7200000}")
  public void maintainExpiredApiKeys() {
    sessionManagementApi.startTechnicalSession(technicalUserProvider.get());
    apiKeyApi.maintainExpiredApiKeys();
  }

}
