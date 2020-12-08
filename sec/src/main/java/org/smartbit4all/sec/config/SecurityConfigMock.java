package org.smartbit4all.sec.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@EnableWebSecurity
public class SecurityConfigMock extends WebSecurityConfigurerAdapter {

  private static final String LOGIN_PROCESSING_URL = "/login";
  private static final String LOGIN_FAILURE_URL = "/login?error";
  private static final String LOGIN_URL = "/login";
  private static final String LOGOUT_SUCCESS_URL = "/login";
  
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(getAuthenticationProvider());
  }
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
//      http
//        .authorizeRequests()
//          .anyRequest().authenticated()
//          .and()
//          .formLogin().loginPage(LOGIN_URL).permitAll()
//            .loginProcessingUrl(LOGIN_PROCESSING_URL)
//            .failureUrl(LOGIN_FAILURE_URL)
//          .and()
//          .logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)
//          .and()
//          .csrf().disable();
    http
    .authorizeRequests()
      .anyRequest().authenticated()
      .and()
      .formLogin()
        .defaultSuccessUrl("/", true)
      .and()
      .csrf().disable();
  }
  
  private AuthenticationProvider getAuthenticationProvider() {
    return new AuthenticationProvider() {

      @Override
      public Authentication authenticate(Authentication authentication)
          throws AuthenticationException {
        User user = new User("Mock", "", AuthorityUtils.createAuthorityList("ADMIN"));
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
      }

      @Override
      public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
      }
    };
  }
  
}