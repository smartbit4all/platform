package org.smartbit4all.api.session.restserver.config;

import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.session.restserver.SessionApiController;
import org.smartbit4all.api.session.restserver.SessionApiDelegate;
import org.smartbit4all.api.session.restserver.impl.SessionApiDelegateImpl;
import org.smartbit4all.sec.session.AuthenticationUserProviders;
import org.smartbit4all.sec.session.UserSessionApiSecImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The smartbit4all platform rest server api config.
 * 
 * @author Roland Fényes
 */
@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class SessionSrvRestConfig {

  @Bean
  public SessionApiDelegate sessionApiDelegate() {
    return new SessionApiDelegateImpl();
  }

  @Bean
  public SessionApiController sessionApiController(SessionApiDelegate delegate) {
    return new SessionApiController(delegate);
  }

  @Bean
  @ConditionalOnMissingClass("org.smartbit4all.api.session.UserSessionApi")
  UserSessionApi userSessionApi() {
    return new UserSessionApiSecImpl(
        AuthenticationUserProviders.anonymousUserProvider(),
        AuthenticationUserProviders.sessionBasedUserProvider());
  }

}
