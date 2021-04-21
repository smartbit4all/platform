package org.smartbit4all.api.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.org.bean.Group;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public abstract class OrgApiImpl implements OrgApi {


  private Map<String, List<Group>> inheritedGroups = new HashMap<>();

  private static final String SUBGROUP_PREFIX = "subgroup.";

  
  public OrgApiImpl(Environment env) {

    if (env instanceof ConfigurableEnvironment) {
      for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env)
          .getPropertySources()) {
        if (propertySource instanceof EnumerablePropertySource) {
          for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
            if (key.startsWith(SUBGROUP_PREFIX)) {
              String groupName = key.substring(SUBGROUP_PREFIX.length());
              List<Group> groupsList = new ArrayList<Group>();
              String property = (String) propertySource.getProperty(key);
              String[] groups = property.split(",");
              for (String group : groups) {
                groupsList.add(createGroup(group));
              }

              inheritedGroups.put(groupName, groupsList);
            }
          }
        }
      }
    }
  }

  protected abstract Group createGroup(String group);
  
  protected Set<Group> getAdditionalVirtualGroups(List<Group> groups) {
    Set<Group> additionalGroups = new HashSet<>();
    for (Group group : groups) {
      List<Group> virtualGroups = inheritedGroups.get(group.getName());
      if (virtualGroups != null) {
        additionalGroups.addAll(virtualGroups);
      }
    }
    return additionalGroups;
  }
 
  

}
