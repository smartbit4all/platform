package org.smartbit4all.sec.token;

import java.time.OffsetDateTime;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

public interface SessionTokenHandler {

  String createToken(String subject, OffsetDateTime expiration);

  String getSubject(String token);

  Date getExpiration(String token);

  boolean isTokenValid(String token);

  String getTokenFromRequest(HttpServletRequest request);

}
