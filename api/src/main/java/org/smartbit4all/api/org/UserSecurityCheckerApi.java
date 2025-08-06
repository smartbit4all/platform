package org.smartbit4all.api.org;

import java.net.URI;
import org.smartbit4all.core.object.ObjectNode;

public interface UserSecurityCheckerApi {

  static final String API =
      "org.smartbit4all.api.org.UserSecurityCheckerApi";

  static final String PASSWORD_EXPIRED = "passwordExpired";

  static final String PASSWORD_EXPIRE_SOON = "passwordExpiredSoon";

  static final String USER_INACTIVE = "userInactive";

  String SCHEMA = "securityPolicy";
  String USER_SECURITY_POLICIES = "user-security-policy-list";
  String MDM_NAME = "user-security-policies";

  String LAST_ACCESS_IN_ORG = "last_access";

  URI updateOrCreateUserLastAccess(URI userUri, String path);

  void updateOrCreateUserLastAccess(ObjectNode userNode, String path);

  URI resetLoginAttemptAndBlockDate(URI userUri);

  boolean shouldRemindToChangePassword(URI userUri);

  void checkUsersBySecurityPolicy();

  boolean isUserBlocked(URI userUri);

  URI increaseLoginAttemptAndCheck(String userName);

  boolean checkPassword(String password);

}
