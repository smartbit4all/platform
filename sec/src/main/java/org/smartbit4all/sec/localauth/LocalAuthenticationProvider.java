package org.smartbit4all.sec.localauth;

import java.util.Collection;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

/**
 * Authenticates username-password credentials and creates a LocalAuthentication token.
 */
public class LocalAuthenticationProvider implements AuthenticationProvider {

  private static final Logger log = LoggerFactory.getLogger(LocalAuthenticationProvider.class);

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private Function<User, Collection<GrantedAuthority>> roleProvider =
      u -> AuthorityUtils.NO_AUTHORITIES;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    User user = orgApi.getUserByUsername(username);

    if (isUserWithPasswordInvalid(user, password)) {
      log.debug("Login attempt has failed because of bad credentials with user [{}]", username);
      throw new BadCredentialsException("Unknown user or invalid password!");
    }

    return new UsernamePasswordAuthenticationToken(user, "", roleProvider.apply(user));
  }

  protected boolean isUserWithPasswordInvalid(User user, String password) {
    return user == null
        || ObjectUtils.isEmpty(password)
        || !passwordEncoder.matches(password, user.getPassword());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

  public void setRoleProvider(Function<User, Collection<GrantedAuthority>> roleProvider) {
    this.roleProvider = roleProvider;
  }

}
