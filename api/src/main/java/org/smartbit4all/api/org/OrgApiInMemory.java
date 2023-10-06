package org.smartbit4all.api.org;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.gateway.SecurityGateways;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

/**
 * Generic in-memory implementation of OrgApi. Not thread-safe, use it only for demo and test
 * purposes!
 *
 * @author Attila Mate
 * @since 2021.04.19.
 */
public class OrgApiInMemory extends OrgApiImpl {

  private static final String ORGAPI_USERS = "orgapi.users";

  protected Map<URI, User> users;

  protected Map<URI, Group> groups;

  public OrgApiInMemory(Environment env) {
    super(env);
    users = new HashMap<>();
    groups = new HashMap<>();

    loadUsersAndGroups(env);
  }

  // TODO Use ConfigurationProperties:
  // https://www.baeldung.com/configuration-properties-in-spring-boot
  protected void loadUsersAndGroups(Environment env) {
    if (!(env instanceof ConfigurableEnvironment)) {
      return;
    }

    for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
      if (propertySource instanceof EnumerablePropertySource) {
        for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
          if (key.startsWith(ORGAPI_USERS)) {
            // e.g. "orgapi.users.username.name"

            // "username.name"
            String userNameProperty = key.substring(ORGAPI_USERS.length() + 1);
            int indexOf = userNameProperty.lastIndexOf(".");

            // "username"
            String userName = userNameProperty.substring(0, indexOf);

            // "name"
            String property = userNameProperty.substring(indexOf + 1);
            String propertyValue = (String) propertySource.getProperty(key);
            User user = users.get(getUserUri(userName));

            if (user == null) {
              user = addTestUser(null, userName, null);
            }

            if ("email".equals(property)) {
              user.setEmail(propertyValue);
            } else if ("password".equals(property)) {
              user.setPassword(propertyValue);
            } else if ("name".equals(property)) {
              user.setName(propertyValue);
            } else if ("groups".equals(property)) {
              String[] groupNames = propertyValue.split(",");
              for (String groupName : groupNames) {
                URI groupUri = getGroupUri(groupName);
                Group group = groups.get(groupUri);
                if (group == null) {
                  addTestGroup(groupName, user);
                } else {
                  group.addChildrenItem(user.getUri());
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  protected Group localGroupOf(String groupName) {
    return new Group().name(groupName).uri(getGroupUri(groupName));
  }

  protected URI getGroupUri(String groupName) {
    return SecurityGateways.generateURI(groupName);
  }

  public User addTestUser(String name, String userName, String email) {
    User newUser = createUser(name, userName, email);

    users.put(newUser.getUri(), newUser);

    return newUser;
  }

  private User createUser(String name, String userName, String email) {
    URI userUri = getUserUri(userName);

    return new User().uri(userUri).name(name).username(userName).email(email);
  }

  protected URI getUserUri(String userName) {
    URI userUri = URI.create("local:/user#" + userName);
    return userUri;
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
  public List<User> getUsers(List<URI> userUris) {
    return users.values().stream().filter(user -> userUris.contains(user.getUri()))
        .collect(Collectors.toList());
  }

  @Override
  public List<User> getActiveUsers() {
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
    Group newGroup = localGroupOf(name);

    for (User user : users) {
      newGroup.addChildrenItem(user.getUri());
    }

    groups.put(newGroup.getUri(), newGroup);

    return newGroup;
  }

  @Override
  public List<Group> getGroupsOfUser(URI userUri) {
    List<Group> result = groups.values().stream()
        .filter(group -> group.getChildren().contains(userUri)).collect(Collectors.toList());

    return result;
  }

  @Override
  public Group getGroup(URI groupUri) {
    return groups.get(groupUri);
  }

  @Override
  public InputStream getUserImage(URI userUri) {
    return null;
  }

  @Override
  public InputStream getGroupImage(URI groupUri) {
    return null;
  }

}
