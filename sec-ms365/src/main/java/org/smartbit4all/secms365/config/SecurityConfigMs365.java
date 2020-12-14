/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
