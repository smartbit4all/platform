package org.smartbit4all.sec.config;

import org.smartbit4all.sec.jwt.JwtRequestFilter;
import org.smartbit4all.sec.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformJwtConfig {

  @Bean
  JwtRequestFilter jwtRequestFilter() {
    return new JwtRequestFilter();
  }

  @Bean
  JwtUtil jwtUtil() {
    return new JwtUtil();
  }

}
