package org.smartbit4all.api.org;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.AccountInfo;

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

  public static Boolean securityPredicate(OrgApi orgApi, Supplier<User> currentUserProvider,
      SessionApi sessionApi, SecurityGroup securityGroup, URI userUri) {
    if (sessionApi != null) {
      return securityPredicate(sessionApi, securityGroup);
    }
    if (userUri == null) {
      if (currentUserProvider == null || currentUserProvider.get() == null) {
        return false;
      }
      userUri = currentUserProvider.get().getUri();
    }
    List<Group> groupsOfUser = orgApi.getGroupsOfUser(userUri);
    if (groupsOfUser == null) {
      return false;
    }
    return groupsOfUser.parallelStream().anyMatch(
        g -> securityGroup.getName().equals(g.getName()));
  }

  public static Boolean securityPredicate(SessionApi sessionApi, SecurityGroup securityGroup) {
    return securityPredicate(sessionApi, securityGroup.getName());
  }

  public static Boolean securityPredicate(SessionApi sessionApi, String securityGroupName) {
    if (securityGroupName == null) {
      return Boolean.FALSE;
    }
    return sessionApi.getAuthentications().stream()
        .map(AccountInfo::getRoles)
        .flatMap(List::stream)
        .anyMatch(
            g -> securityGroupName.equals(g));
  }
}
