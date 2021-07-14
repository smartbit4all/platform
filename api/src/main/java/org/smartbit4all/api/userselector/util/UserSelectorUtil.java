package org.smartbit4all.api.userselector.util;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.userselector.bean.UserMultiSelector;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.api.userselector.bean.UserSingleSelector;

public class UserSelectorUtil {
  
  public static UserSingleSelector createUserSingleSelector(List<User> users, List<Group> groups, URI selectedUserUri) {
    List<UserSelector> allUserSelector = collectSelectors(users, groups);
    UserSelector selected = allUserSelector.stream()
        .filter(us -> us.getUri().equals(selectedUserUri))
        .findFirst()
        .orElse(null);
    
    return new UserSingleSelector().selectors(allUserSelector).selected(selected);
  }
  
  public static UserMultiSelector createUserMultiSelector(List<User> users, List<Group> groups, List<URI> selectedUserURIs) {
    List<UserSelector> allUserSelector = collectSelectors(users, groups);
    List<UserSelector> selected = allUserSelector.stream()
        .filter(us -> selectedUserURIs.contains(us.getUri()))
        .collect(Collectors.toList());
    
    return new UserMultiSelector().selectors(allUserSelector).selected(selected);
  }

  private static List<UserSelector> collectSelectors(List<User> users, List<Group> groups) {
    List<UserSelector> userSelectorList = users.stream()
        .map(u -> createUserSelector(u))
        .collect(Collectors.toList());
    
    userSelectorList.addAll(groups.stream()
            .map(g -> createUserSelector(g))
            .collect(Collectors.toList()));
    
    return userSelectorList;
  }

  private static UserSelector createUserSelector(User user) {
    return new UserSelector()
        .kind(UserSelector.KindEnum.USER)
        .uri(user.getUri())
        .displayName(user.getName() + " (" + user.getEmail() + ")");
  }

  private static UserSelector createUserSelector(Group group) { return new UserSelector()
        .kind(UserSelector.KindEnum.GROUP)
        .uri(group.getUri())
        .displayName(group.getName());
  }
}
