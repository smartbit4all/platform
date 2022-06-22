package org.smartbit4all.api.session.restserver.impl;

import java.util.Date;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.session.bean.StartSessionResult;
import org.smartbit4all.api.session.restserver.SessionApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class SessionApiDelegateImpl implements SessionApiDelegate {

  private String SECRET_KEY = "it's not a secret anymore";
  
  @Autowired
  UserSessionApi userSessionApi;
  private String createToken(String subject) {
    /**
     * This method creates the JwtToken based on the followings: - claims (Map<String, Object>) -
     * subject (username)
     *
     * The JwtToken generated by this method contains the claims, the subject, the creation date, an
     * expiration date (currently 10 hours), and all of this is signed with the SECRET_KEY.
     *
     * @Return String - JwtToken
     */
    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
  }
  
  @Override
  public ResponseEntity<StartSessionResult> startSession() throws Exception {
    String token = createToken("anonymus");
    Session session = userSessionApi.startSession(null, token);
    
    return ResponseEntity.ok(new StartSessionResult().token(token));
  }
  
}
