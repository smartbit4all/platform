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

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadOAuth2AuthorizationRequestResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigMs365 {

  @Autowired
  private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

  @Autowired
  ApplicationContext applicationContext;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    final ClientRegistrationRepository clientRegistrationRepository =
        applicationContext.getBean(ClientRegistrationRepository.class);
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(it -> it.anyRequest().authenticated())
        .oauth2Login(it -> it
                .userInfoEndpoint(e -> e.oidcUserService(oidcUserService))
                .authorizationEndpoint(e -> e.authorizationRequestResolver(
                    new AadOAuth2AuthorizationRequestResolver(clientRegistrationRepository, null)))
            /* FIXME: .failureHandler(null) */)
        .logout(it -> it.invalidateHttpSession(true));
    return http.build();
  }
}
