package org.smartbit4all.sec.scheduling;

import java.util.Objects;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
        createSchedulerSecurityContext(userSessionApi, technicalUserProvider);
    return new DelegatingSecurityContextScheduledExecutorService(
        taskScheduler.getScheduledExecutor(), schedulerContext);

  }

  private static SecurityContext createSchedulerSecurityContext(UserSessionApi userSessionApi,
      TechnicalUserProvider technicalUserProvider) {
    SecurityContext createEmptyContext = SecurityContextHolder.createEmptyContext();
    User technicalUser = technicalUserProvider.getTechnicalUser();
    Objects.requireNonNull(technicalUser, "technicalUser can not be null!");
    Session session = userSessionApi.startSession(technicalUser);
    Authentication auth = new UsernamePasswordAuthenticationToken(session, null);
    createEmptyContext.setAuthentication(auth);
    return createEmptyContext;
  }

  public static interface TechnicalUserProvider {
    User getTechnicalUser();
  }

}
