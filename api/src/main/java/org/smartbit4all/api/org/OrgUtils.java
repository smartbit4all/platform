package org.smartbit4all.api.org;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApi;

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

  public static Boolean securityPredicate(OrgApi orgApi, UserSessionApi userSessionApi,
      SecurityGroup securityGroup, URI userUri) {
    if (userUri == null) {
      if (userSessionApi == null || userSessionApi.currentUser() == null) {
        return false;
      }
      userUri = userSessionApi.currentUser().getUri();
    }
    List<Group> groupsOfUser = orgApi.getGroupsOfUser(userUri);
    if (groupsOfUser == null) {
      return false;
    }
    return groupsOfUser.parallelStream().anyMatch(
        g -> securityGroup.getName().equals(g.getName()));
  }

}
