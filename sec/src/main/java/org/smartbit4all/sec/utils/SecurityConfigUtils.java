package org.smartbit4all.sec.utils;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.ExceptionTranslationFilter;

public class SecurityConfigUtils {

  private SecurityConfigUtils() {}

  public static void disableRedirectOnAccessDeniedForAnonymous(HttpSecurity http) throws Exception {
    http.exceptionHandling()
        .addObjectPostProcessor(new ObjectPostProcessor<ExceptionTranslationFilter>() {
          @Override
          public <O extends ExceptionTranslationFilter> O postProcess(O object) {
            object.setAuthenticationTrustResolver(new DisabledAuthenticationTrustResolver());
            return object;
          }
        });
  }

  private static class DisabledAuthenticationTrustResolver implements AuthenticationTrustResolver {

    @Override
    public boolean isAnonymous(Authentication authentication) {
      return false;
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
      return false;
    }

  }
}
