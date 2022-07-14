package org.smartbit4all.sec.jwt;

import java.util.Date;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.User;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * This class is responsible for handling the JwtToken.
 */
public class JwtUtil {

  private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

  @Value("${jwt.secret:secret}")
  private String secretKey;

  @Value("${jwt.timeout:10}")
  private int timeout;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claims != null ? claimsResolver.apply(claims) : null;
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (Exception e) {
      log.debug("Error when parsing JWT token", e);
    }
    return null;
  }

  private boolean isTokenExpired(String token) {
    Date expiration = extractExpiration(token);
    return expiration != null ? expiration.before(new Date()) : true;
  }

  public String generateToken(User user) {
    return createToken(user.getUsername());
  }

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
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * timeout))
        .signWith(SignatureAlgorithm.HS256, secretKey).compact();
  }

  public boolean validateToken(String token, User user) {
    final String username = extractUsername(token);
    return username != null && user != null &&
        username.equals(user.getUsername()) &&
        !isTokenExpired(token);
  }

}