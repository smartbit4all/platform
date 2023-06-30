package org.smartbit4all.sec.localauth;

import org.smartbit4all.api.org.PasswordEncoderApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderApiImpl implements PasswordEncoderApi {

  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  public String encode(CharSequence rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }



}
