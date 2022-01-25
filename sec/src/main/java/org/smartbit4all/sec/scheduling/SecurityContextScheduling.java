package org.smartbit4all.sec.scheduling;

import java.util.Objects;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.sec.utils.SecurityContextUtility;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.context.SecurityContext;

public class SecurityContextScheduling {

  private SecurityContextScheduling() {}

  public static DelegatingSecurityContextScheduledExecutorService createContextAwareScheduler(
      ThreadPoolTaskScheduler taskScheduler,
      UserSessionApi userSessionApi,
      TechnicalUserProvider technicalUserProvider) {
    Objects.requireNonNull(taskScheduler, "taskScheduler can not be null!");
    Objects.requireNonNull(userSessionApi, "userSessionApi can not be null!");
    Objects.requireNonNull(technicalUserProvider, "technicalUserProvider can not be null!");

    SecurityContext schedulerContext =
        SecurityContextUtility.createSecurityContext(userSessionApi, technicalUserProvider);
    return new DelegatingSecurityContextScheduledExecutorService(
        taskScheduler.getScheduledExecutor(), schedulerContext);

  }

  public static interface TechnicalUserProvider {
    User getTechnicalUser();
  }

}
