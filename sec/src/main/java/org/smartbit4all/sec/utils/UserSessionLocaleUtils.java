package org.smartbit4all.sec.utils;

import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.sec.session.SessionManagementApiImpl;
import org.springframework.util.StringUtils;

public abstract class UserSessionLocaleUtils {

  public static final String USER_LOCALE_ATTRUBUTE = "USER_LOCALE";

  private UserSessionLocaleUtils() {}

  /**
   * This utility sets up the {@link SessionManagementApiImpl} to handle the logged in user's locale
   * attribute. <br/>
   * 
   * Setting the locale when a user is logged in and the locale is being modified this locale is
   * also set into the user's attributes.<br/>
   * On login: if the user had a previously set locale we set this to the session.
   */
  public static void handleLocaleChangeWithUser(SessionManagementApiImpl sessionManagementApiImpl,
      ObjectApi objectApi) {
    sessionManagementApiImpl.addLocaleChangeListener((sessionUri, locale) -> {
      ObjectNode userNode = objectApi
          .load(sessionUri).ref(Session.USER).get();
      if (userNode != null) {
        userNode.modify(User.class, u -> u.putAttributesItem(USER_LOCALE_ATTRUBUTE, locale));
        objectApi.save(userNode);
      }
    });

    sessionManagementApiImpl.addUserChangeListener((sessionUri, userUri) -> {
      ObjectNode userNode = objectApi.load(userUri);
      ObjectNode sessionNode = objectApi.load(sessionUri);
      String locale = userNode.getValueAsMap(String.class, User.ATTRIBUTES)
          .get(USER_LOCALE_ATTRUBUTE);
      if (!StringUtils.isEmpty(locale)) {
        sessionNode.setValue(locale, Session.LOCALE);
        objectApi.save(sessionNode);
      } else {
        userNode.modify(User.class, u -> u.putAttributesItem(USER_LOCALE_ATTRUBUTE,
            sessionNode.getValueAsString(Session.LOCALE)));
        objectApi.save(userNode);
      }
    });
  }

}
