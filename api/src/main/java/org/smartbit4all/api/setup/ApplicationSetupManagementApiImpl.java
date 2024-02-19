package org.smartbit4all.api.setup;

import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.springframework.scheduling.annotation.Scheduled;

public class ApplicationSetupManagementApiImpl extends PrimaryApiImpl<ApplicationSetupApi>
    implements ApplicationSetupManagementApi {

  public ApplicationSetupManagementApiImpl() {
    super(ApplicationSetupApi.class);
  }

  @Override
  @Scheduled(initialDelayString = "${applicationsetup.schedule.initdelay:2000}",
      fixedDelayString = "${applicationsetup.schedule.fixeddelay:60000}")
  public void scheduleSetupFunctions() {
    // Map<String, ApplicationSetupApi> apis = getContributionApis().values().stream().flatMap(api
    // -> ReflectionUtility.);
  }

}
