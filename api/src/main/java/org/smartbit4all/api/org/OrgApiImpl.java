package org.smartbit4all.api.org;

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
              List<Group> groupsList = new ArrayList<Group>();
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
            securityGroup.setApi(this);
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
        Storage<Group> storage = storageApi.get(Group.class);
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



}
