package org.smartbit4all.secms365.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import com.microsoft.azure.spring.autoconfigure.aad.AADAuthenticationFailureHandler;
import com.microsoft.azure.spring.autoconfigure.aad.AADOAuth2AuthorizationRequestResolver;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigMs365 extends WebSecurityConfigurerAdapter {
  @Autowired
  private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

  @Autowired
  ApplicationContext applicationContext;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
      final ClientRegistrationRepository clientRegistrationRepository =
              applicationContext.getBean(ClientRegistrationRepository.class);
      http
        .csrf().disable()
        .authorizeRequests()
          .anyRequest().authenticated()
        .and()
          .oauth2Login()
            .userInfoEndpoint()
              .oidcUserService(oidcUserService)
            .and()
              .authorizationEndpoint()
                .authorizationRequestResolver(
                  new AADOAuth2AuthorizationRequestResolver(clientRegistrationRepository))
            .and()
              .failureHandler(new AADAuthenticationFailureHandler())
            .and()
              .logout().invalidateHttpSession(true);
  }
}