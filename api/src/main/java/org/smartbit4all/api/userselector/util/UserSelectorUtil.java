package org.smartbit4all.api.userselector.util;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.api.userselector.bean.UserSelectors;

public class UserSelectorUtil {
  
  public static UserSelectors createUserSelectors(List<User> users, List<Group> groups, URI selectedUserUri) {
    List<UserSelector> userSelectorList = collectSelectors(users, groups);
    UserSelector selected = userSelectorList.stream()
        .filter(us -> us.getUri().equals(selectedUserUri))
        .findFirst()
        .orElse(null);
    
    return new UserSelectors().selectors(userSelectorList).selected(selected);
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
