/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.core.object.ApiObjectRef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The basic implementation of the {@link NavigationApi}.
 * 
 * @author Peter Boros
 */
public abstract class NavigationImpl implements NavigationApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(NavigationImpl.class);

  protected String name;

  /**
   * The available navigation configs autowired by the Spring. If we include a new functional module
   * adding new config they will be available immediately.
   */
  @Autowired(required = false)
  private List<NavigationConfig> navigationConfigs;

  /**
   * Collects every named navigation config available in the current application, therefore these
   * configs can be get by name easily.
   */
  private Map<String, NavigationConfig> navConfigsByName = new HashMap<>();

  public NavigationImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (navigationConfigs != null) {
      for (NavigationConfig navigationConfig : navigationConfigs) {
        if (navigationConfig.getName() != null) {
          navConfigsByName.put(navigationConfig.getName(), navigationConfig);
        } else {
          log.error(
              "The {} navigation config is unnamed, it will be skipped from named config list!",
              navigationConfig);
        }
      }
    }
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris, Consumer<URI> nodeChangedListener) {
    return navigate(objectUri, associationMetaUris);
  }

  @Override
  public Optional<ApiObjectRef> loadObject(URI entryMetaUri, URI objectUri) {
    return Optional.empty();
  }

  @Override
  public Navigation start(String navigationConfigName, List<NavigationRootNode> rootNodes) {
    NavigationConfig navigationConfig = navConfigsByName.get(navigationConfigName);
    if (navigationConfig == null) {
      log.debug(
          "Unable to start {} navigation, missing configuration by name. Extend Configuration!",
          navigationConfigName);
      return null;
    }
    Navigation result = new Navigation(navigationConfig, this);
    if (rootNodes != null) {
      for (NavigationRootNode navigationRootNode : rootNodes) {
        result.addRootNode(navigationRootNode.getEntryMetaUri(), navigationRootNode.getObjectUri());
      }
    }
    return result;
  }

}
