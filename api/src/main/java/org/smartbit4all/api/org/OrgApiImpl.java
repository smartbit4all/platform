package org.smartbit4all.api.org;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.setting.LocaleString;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public abstract class OrgApiImpl implements OrgApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(LocaleSettingApi.class);

  private Map<String, List<Group>> inheritedGroups = new HashMap<>();

  private static final String SUBGROUP_PREFIX = "subgroup.";

  public static final String ORG_SCHEME = "org";

  @Autowired(required = false)
  private List<SecurityOption> securityOptions;

  @Autowired
  private StorageApi storageApi;

  public OrgApiImpl(Environment env) {

    if (env instanceof ConfigurableEnvironment) {
      for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env)
          .getPropertySources()) {
        if (propertySource instanceof EnumerablePropertySource) {
          for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
            if (key.startsWith(SUBGROUP_PREFIX)) {
              String groupId = key.substring(SUBGROUP_PREFIX.length());
              List<Group> groupsList = new ArrayList<>();
              String property = (String) propertySource.getProperty(key);
              String[] groups = property.split(",");
              for (String group : groups) {
                groupsList.add(localGroupOf(group));
              }

              inheritedGroups.put(groupId, groupsList);
            }
          }
        }
      }
    }
  }

  /**
   * This function analyze the given class to discover the {@link LocaleString} fields. We add this
   * API for them to enable locale specific behavior for them.
   * 
   * @param clazz
   */
  public void analyzeSecurityOptions(SecurityOption option) {
    // Let's check the static LocaleString
    Field[] fields = option.getClass().getFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      if (field.getType().isAssignableFrom(SecurityGroup.class)) {
        try {
          SecurityGroup securityGroup = (SecurityGroup) field.get(option);
          if (securityGroup != null) {
            securityGroup.setOrgApi(this);
            String key = ReflectionUtility.getQualifiedName(field);
            securityGroup.setName(key);
          }
        } catch (IllegalArgumentException | IllegalAccessException e) {
          log.debug("Unable to access the value of the " + field, e);
        }
      }
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (securityOptions != null) {
      for (SecurityOption securityOption : securityOptions) {
        analyzeSecurityOptions(securityOption);
        Storage storage = storageApi.get(ORG_SCHEME);
        if (storage != null) {
          // Check if the given group exists in the storage
          // storage.load(new URI(storage.))
        }
      }
    }
  }

  protected Group localGroupOf(String groupName) {
    Group group = new Group();
    group.setName(groupName);
    group.setUri(URI.create("local:/group#" + getGroupId(groupName)));
    return group;
  }

  private String getGroupId(String groupName) {
    return groupName.replaceAll("\\s", "");
  }

  protected Set<Group> getAdditionalLocalGroups(List<Group> groups) {
    Set<Group> additionalGroups = new HashSet<>();
    for (Group group : groups) {
      List<Group> virtualGroups = inheritedGroups.get(group.getUri().getFragment());
      if (virtualGroups != null) {
        additionalGroups.addAll(virtualGroups);
      }
    }
    return additionalGroups;
  }

  @Override
  public User getUser(URI userUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<User> getAllUsers() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Group> getAllGroups() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<User> getUsersOfGroup(URI groupUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Group> getGroupsOfUser(URI userUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Group getGroup(URI groupUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Group> getRootGroups() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getUserImage(URI userUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getGroupImage(URI groupUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getUserByUsername(String username) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public URI saveUser(User user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public URI saveGroup(Group group) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addUserToGroup(URI userUri, URI groupUri) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeUser(URI userUri) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeGroup(URI groupUri) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeUserFromGroup(URI userUri, URI groupUri) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addChildGroup(Group parentGroup, Group childGroup) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<Group> getSubGroups(URI groupUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Group getGroupByName(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeSubGroup(URI parentGroupUri, URI childGroupUri) {
    // TODO Auto-generated method stub
  }

  @Override
  public URI updateGroup(Group group) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public URI updateUser(User user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void restoreDeletedUser(URI userUri) {
    // TODO Auto-generated method stub
  }

}
