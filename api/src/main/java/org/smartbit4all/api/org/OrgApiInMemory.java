package org.smartbit4all.api.org;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * Generic in-memory implementation of OrgApi. Not thread-safe, use it only for demo and test
 * purposes!
 * 
 * @author Attila Mate
 * @since 2021.04.19.
 */
public class OrgApiInMemory extends OrgApiImpl {

  protected Map<URI, User> users;

  protected Map<URI, Group> groups;

  protected User currentUser;

  public OrgApiInMemory(Environment env) {
    super(env);
    users = new HashMap<>();
    groups = new HashMap<>();
  }

  @Override
  protected Group createGroup(String name) {
    return new Group().name(name).uri(URI.create("userGroup:/" + name));
  }

  public User addTestUser(String name, String userName, String email) {
    User newUser = createUser(name, userName, email);

    users.put(newUser.getUri(), newUser);

    return newUser;
  }

  private User createUser(String name, String userName, String email) {
    URI userUri = URI.create("user:/" + userName);

    return new User().uri(userUri).name(name).username(userName).email(email);
  }

  @Override
  public List<Group> getAllGroups() {
    return new ArrayList<>(groups.values());
  }

  @Override
  public User getUser(URI userUri) {
    return users.get(userUri);
  }

  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(users.values());
  }

  @Override
  public List<User> getUsersOfGroup(URI groupUri) {
    Group group = groups.get(groupUri);

    Assert.notNull(group, "Group with URI does not exists: " + groupUri);

    List<User> result = new ArrayList<>();
    for (URI childrenUri : group.getChildren()) {
      User user = getUser(childrenUri);

      if (user != null) {
        result.add(user);
      }
    }

    return result;
  }

  public Group addTestGroup(String name, User... users) {
    Group newGroup = createGroup(name);

    for (User user : users) {
      newGroup.addChildrenItem(user.getUri());
    }

    groups.put(newGroup.getUri(), newGroup);

    return newGroup;
  }

  @Override
  public List<Group> getGroupsOfUser(URI userUri) {
    List<Group> result = groups.values().stream().filter(group -> group.getChildren().contains(userUri))
        .collect(Collectors.toList());
    
    result.addAll(getAdditionalVirtualGroups(result));
    return result;
  }

  @Override
  public Group getGroup(URI groupUri) {
    return groups.get(groupUri);
  }

  @Override
  public List<Group> getRootGroups() {
    return groups.values().stream().filter(group -> group.getParent() == null)
        .collect(Collectors.toList());
  }

  @Override
  public InputStream getUserImage(URI userUri) {
    return null;
  }

  @Override
  public InputStream getGroupImage(URI groupUri) {
    return null;
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  @Override
  public User currentUser() {
    return currentUser;
  }

}
