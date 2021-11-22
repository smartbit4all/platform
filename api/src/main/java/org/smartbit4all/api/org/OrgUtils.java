package org.smartbit4all.api.org;

import java.net.URI;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;

public class OrgUtils {

  private OrgUtils() {
    super();
  }

  public static final User createUserByUserName(OrgApi api, User user) {
    if (user != null) {
      User userByUsername = api.getUserByUsername(user.getUsername());
      if (userByUsername == null) {
        URI saveUser = api.saveUser(user);
        return api.getUser(saveUser);
      }
      return userByUsername;
    }
    return null;
  }

  public static final Group applyGroupByName(OrgApi api, Group group, User... users) {
    if (group != null) {
      Group groupByName = api.getGroupByName(group.getName());
      if (groupByName == null) {
        URI uri = api.saveGroup(group);
        groupByName = api.getGroup(uri);
      }
      if (users != null) {
        for (User user : users) {
          User userByUserName = createUserByUserName(api, user);
          api.addUserToGroup(userByUserName.getUri(), groupByName.getUri());
        }
      }
      return groupByName;
    }
    return null;
  }
}
