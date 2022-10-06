package org.smartbit4all.sec.token;

import java.time.OffsetDateTime;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.smartbit4all.sec.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class SessionTokenHandlerJWT implements SessionTokenHandler {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public String createToken(String subject, OffsetDateTime expiration) {
    return jwtUtil.createToken(subject, expiration);
  }

  @Override
  public String getSubject(String token) {
    return jwtUtil.extractSubject(token);
  }

  @Override
  public Date getExpiration(String token) {
    return jwtUtil.extractExpiration(token);
  }

  @Override
  public String getTokenFromRequest(HttpServletRequest request) {
    return jwtUtil.getJwtTokenFromRequest(request);
  }

  @Override
  public boolean isTokenValid(String token) {
    return !ObjectUtils.isEmpty(token)
        && !ObjectUtils.isEmpty(jwtUtil.extractExpiration(token))
        && !ObjectUtils.isEmpty(jwtUtil.extractSubject(token));
  }

}
