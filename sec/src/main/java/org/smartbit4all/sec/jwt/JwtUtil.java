package org.smartbit4all.sec.jwt;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.User;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

  @Value("${jwt.cookie}")
  private String jwtCookie;

  public String extractSubject(String token) {
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
    } catch (ExpiredJwtException e) {
      log.debug("The JWT token has expired!", e);
    } catch (Exception e) {
      log.debug("Error when parsing JWT token", e);
    }
    return null;
  }

  public boolean isTokenExpired(String token) {
    Date expiration = extractExpiration(token);
    return expiration != null ? expiration.before(new Date()) : true;
  }

  public String generateToken(User user) {
    return createToken(user.getUsername());
  }

  public String createToken(String subject) {
    return createToken(subject, null);
  }

  public String createToken(String subject, OffsetDateTime expiration) {
    Objects.requireNonNull(subject, "Subject can not be null!");

    /**
     * This method creates the JwtToken based on the followings: - claims (Map<String, Object>) -
     * subject (username)
     *
     * The JwtToken generated by this method contains the claims, the subject, the creation date, an
     * expiration date (currently 10 hours), and all of this is signed with the SECRET_KEY.
     *
     * @Return String - JwtToken
     */

    if (expiration == null) {
      expiration = OffsetDateTime.now().plusHours(timeout);
    }
    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(OffsetDateTime.MAX.equals(expiration) ? new Date(Long.MAX_VALUE)
            : new Date(expiration.toInstant().toEpochMilli()))
        .setHeaderParam("uuid", UUID.randomUUID()) // make every created token identical
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public boolean validateToken(String token, User user) {
    final String username = extractSubject(token);
    return username != null && user != null && username.equals(user.getUsername())
        && !isTokenExpired(token);
  }

  public String getJwtTokenFromRequest(HttpServletRequest request) {
    String jwt = null;

    final String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      log.trace("Authorization token in header: {}", jwt);
    } else {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
          Cookie cookie = cookies[i];
          if (cookie != null && jwtCookie.equals(cookie.getName())) {
            jwt = cookie.getValue();
            break;
          }
        }
      }
      log.trace("No authorization token in header, using cookie: {}", jwt);
    }
    return jwt;
  }

}
