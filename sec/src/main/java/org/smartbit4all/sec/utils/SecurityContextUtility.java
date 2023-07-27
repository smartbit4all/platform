package org.smartbit4all.sec.utils;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.sec.scheduling.SecurityContextScheduling.TechnicalUserProvider;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

public class SecurityContextUtility {

  private SecurityContextUtility() {}

  public static SecurityContext createSecurityContext(UserSessionApi userSessionApi,
      TechnicalUserProvider technicalUserProvider) {
    SecurityContext createEmptyContext = SecurityContextHolder.createEmptyContext();
    User technicalUser = technicalUserProvider.getTechnicalUser();
    Objects.requireNonNull(technicalUser, "technicalUser can not be null!");
    org.smartbit4all.api.session.Session session = userSessionApi.startSession(technicalUser);
    Authentication auth = new UsernamePasswordAuthenticationToken(session, null);
    createEmptyContext.setAuthentication(auth);
    return createEmptyContext;
  }

  public static AbstractAuthenticationToken setSessionAuthenticationTokenInContext(
      HttpServletRequest request, String sessionUriTxt,
      List<SessionBasedAuthTokenProvider> tokenProviders,
      Function<Session, AbstractAuthenticationToken> anonymousAuthTokenProvider,
      SessionManagementApi sessionManagementApi,
      Logger log) {

    Assert.notNull(request, "request cannot be null");
    Assert.notNull(sessionUriTxt, "sessionUriTxt cannot be null");
    Assert.notNull(tokenProviders, "tokenProviders cannot be null");
    if (anonymousAuthTokenProvider == null) {
      anonymousAuthTokenProvider = s -> null;
    }
    if (log == null) {
      log = LoggerFactory.getLogger(SecurityContextUtility.class);
    }
    Session session = sessionManagementApi.initCurrentSession(URI.create(sessionUriTxt));
    if (session != null) {
      log.trace("Session found to set in security context: {}", session);
      log.trace("Looking for a SessionBasedAuthTokenProvider matching the session.");

      SessionBasedAuthTokenProvider tokenProvider = tokenProviders.stream()
          .filter(p -> p.supports(session))
          .findFirst()
          .orElse(null);

      AbstractAuthenticationToken authentication = null;
      if (tokenProvider == null) {
        if (log.isTraceEnabled()) {
          log.trace(
              "There is no SessionBasedAuthTokenProvider for the given session. Setting anonymous token! session:\n{}",
              session);
        } else {
          log.debug(
              "There is no SessionBasedAuthTokenProvider for the given session. Setting anonymous token!");
        }
        authentication = anonymousAuthTokenProvider.apply(session);
      } else {
        authentication = tokenProvider.getToken(session);
      }

      Objects.requireNonNull(authentication,
          "The provided authentication token can not be null!");

      log.trace("AuthenticationToken has been created for the session with type [{}]",
          authentication.getClass().getName());

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      setParameter(session, SessionApi.FULLURL, getFullURL(request));
      setParameter(session, SessionApi.REFERER, getReferer(request));
      setParameter(session, SessionApi.CLIENTIPADDR, getClientIpAddr(request));
      setParameter(session, SessionApi.CLIENTOS, getClientOS(request));
      setParameter(session, SessionApi.CLIENTBROWSER, getClientBrowser(request));
      setParameter(session, SessionApi.USERAGENT, getUserAgent(request));

      return authentication;
    }
    return null;
  }

  private static void setParameter(Session session, String key, String value) {
    session.getParameters().put(key, value == null ? StringConstant.UNKNOWN : value);
  }

  public static BiFunction<User, Authentication, AccountInfo> getDefaultAccountInfoProvider(
      OrgApi orgApi) {
    return (user, originalAuthToken) -> new AccountInfo()
        .userName(user.getUsername())
        .displayName(user.getName())
        .roles(orgApi.getGroupsOfUser(user.getUri()).stream()
            .map(Group::getName)
            .collect(Collectors.toList()))
        .parameters(user.getAttributes());

  }

  public static String getReferer(HttpServletRequest request) {
    final String referer = request.getHeader("referer");
    return referer;
  }

  public static String getFullURL(HttpServletRequest request) {
    final StringBuffer requestURL = request.getRequestURL();
    final String queryString = request.getQueryString();

    return queryString == null ? requestURL == null ? StringConstant.UNKNOWN : requestURL.toString()
        : requestURL.append('?')
            .append(queryString)
            .toString();
  }

  // http://stackoverflow.com/a/18030465/1845894
  public static String getClientIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  // http://stackoverflow.com/a/18030465/1845894
  public static String getClientOS(HttpServletRequest request) {
    final String browserDetails = request.getHeader("User-Agent");

    // =================OS=======================
    final String lowerCaseBrowser =
        browserDetails == null ? StringConstant.UNKNOWN : browserDetails.toLowerCase();
    if (lowerCaseBrowser.contains("windows")) {
      return "Windows";
    } else if (lowerCaseBrowser.contains("mac")) {
      return "Mac";
    } else if (lowerCaseBrowser.contains("x11")) {
      return "Unix";
    } else if (lowerCaseBrowser.contains("android")) {
      return "Android";
    } else if (lowerCaseBrowser.contains("iphone")) {
      return "IPhone";
    } else {
      return "UnKnown, More-Info: " + browserDetails;
    }
  }

  // http://stackoverflow.com/a/18030465/1845894
  public static String getClientBrowser(HttpServletRequest request) {
    final String browserDetails = request.getHeader("User-Agent");
    final String user =
        browserDetails == null ? StringConstant.UNKNOWN : browserDetails.toLowerCase();

    String browser = "";

    // ===============Browser===========================
    if (user.contains("msie")) {
      String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
      browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
    } else if (user.contains("safari") && user.contains("version")) {
      browser = (browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]).split(
          "/")[0] + "-"
          + (browserDetails.substring(
              browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
    } else if (user.contains("opr") || user.contains("opera")) {
      if (user.contains("opera"))
        browser = (browserDetails.substring(browserDetails.indexOf("Opera")).split(" ")[0]).split(
            "/")[0] + "-"
            + (browserDetails.substring(
                browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
      else if (user.contains("opr"))
        browser =
            ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]).replace("/",
                "-")).replace(
                    "OPR", "Opera");
    } else if (user.contains("chrome")) {
      browser = (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0])
          .replace("/", "-");
    } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)
        || (user.indexOf(
            "mozilla/4.7") != -1)
        || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf(
            "mozilla/4.08") != -1)
        || (user.indexOf("mozilla/3") != -1)) {
      // browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
      browser = "Netscape-?";

    } else if (user.contains("firefox")) {
      browser = (browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0])
          .replace("/", "-");
    } else if (user.contains("rv")) {
      browser = "IE";
    } else {
      browser = "UnKnown, More-Info: " + browserDetails;
    }

    return browser;
  }

  public static String getUserAgent(HttpServletRequest request) {
    String header = request.getHeader("User-Agent");
    return header == null ? StringConstant.UNKNOWN : header;
  }

}
