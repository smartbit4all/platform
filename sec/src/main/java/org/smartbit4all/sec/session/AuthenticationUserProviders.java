package org.smartbit4all.sec.session;

import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.Session;
import org.springframework.security.core.Authentication;

public class AuthenticationUserProviders {

  private AuthenticationUserProviders() {}

  /**
   * Returns an {@link AuthenticationUserProvider} which handles the case when the User object
   * itself is stored in the Authentication token.
   */
  public static AuthenticationUserProvider simpleUserProvider() {
    return new AuthenticationUserProvider() {

      @Override
      public boolean supports(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal == null) {
          return false;
        }
        Class<?> principalClass = principal.getClass();
        return User.class.isAssignableFrom(principalClass);
      }

      @Override
      public User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
      }

      @Override
      public Session getSession(Authentication authentication) {
        return null;
      }
    };
  }

  public static AuthenticationUserProvider sessionBasedUserProvider() {
    return new AuthenticationUserProvider() {

      @Override
      public boolean supports(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal == null) {
          return false;
        }
        Class<?> principalClass = principal.getClass();
        return Session.class.isAssignableFrom(principalClass);
      }

      @Override
      public User getUser(Authentication authentication) {
        Session session = (Session) authentication.getPrincipal();
        return session.getUser();
      }

      @Override
      public Session getSession(Authentication authentication) {
        return (Session) authentication.getPrincipal();
      }
    };
  }

  public static AuthenticationUserProvider springSecUserProvider(OrgApi orgApi) {
    return new AuthenticationUserProvider() {

      @Override
      public User getUser(Authentication authentication) {
        org.springframework.security.core.userdetails.User springSecUser =
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User user = getUserByUsername(springSecUser.getUsername());
        return user;
      }

      @Override
      public boolean supports(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal == null) {
          return false;
        }
        Class<?> principalClass = principal.getClass();
        return org.springframework.security.core.userdetails.User.class
            .isAssignableFrom(principalClass);
      }

      private User getUserByUsername(String username) {
        // FIXME: user could be cached by the username
        if (orgApi != null) {
          return orgApi.getUserByUsername(username);
        }
        return null;
      }

      @Override
      public Session getSession(Authentication authentication) {
        return null;
      }
    };
  }

  public static AuthenticationUserProvider technicalUserProvider(User technicalUser) {
    return new AuthenticationUserProvider() {

      @Override
      public User getUser(Authentication authentication) {
        return technicalUser;
      }

      @Override
      public boolean supports(Authentication authentication) {
        return authentication == null;
      }

      @Override
      public Session getSession(Authentication authentication) {
        return null;
      }
    };
  }

}
