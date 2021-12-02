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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.core.object.ApiObjectRef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * This primary implementation is responsible for routing and merging the navigation requests among
 * the {@link NavigationApi} implementations. So if we have a {@link NavigationApi} implementation
 * it can contribute to any {@link Navigation} instance based on the {@link NavigationConfig} it
 * has.
 * 
 * @author Peter Boros
 */
public final class NavigationPrimary extends NavigationImpl implements InitializingBean {


  private static final Logger log = LoggerFactory.getLogger(NavigationPrimary.class);

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

  /**
   * The registered {@link NavigationApi}s by name. The name is the scheme part of the
   * {@link NavigationAssociationMeta#getUri()}. The routing is based on this.
   */
  Map<String, NavigationApi> apiByName;

  @Autowired(required = false)
  List<NavigationApi> apis;

  public NavigationPrimary() {
    super(NavigationPrimary.class.getTypeName());
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris, Consumer<URI> nodeChangedListener) {
    // In this case the scheme of the uri in the NavigationAssociationMeta identifies the api to
    // delegate.
    if (associationMetaUris == null || associationMetaUris.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<NavigationApi, List<URI>> assocByApi = new HashMap<>();
    for (URI associationMetaUri : associationMetaUris) {
      NavigationApi api = api(associationMetaUri);
      if (api != null) {
        List<URI> assocList = assocByApi.computeIfAbsent(api, k -> new ArrayList<>());
        assocList.add(associationMetaUri);
      }
    }
    if (assocByApi.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<URI, List<NavigationReferenceEntry>> result = new HashMap<>();
    for (Entry<NavigationApi, List<URI>> requestEntry : assocByApi
        .entrySet()) {
      // Call the given api with the list as parameter and merge the result.
      Map<URI, List<NavigationReferenceEntry>> navigate =
          requestEntry.getKey().navigate(objectUri, requestEntry.getValue(), nodeChangedListener);
      result.putAll(navigate);
    }
    return result;
  }

  /**
   * This uri is a meta uri from the navigation could be association or entry meta uri.
   * 
   * @param uri
   * @return
   */
  private NavigationApi api(URI uri) {
    return apiByName.get(uri.getScheme());
  }

  @Override
  public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) {
    NavigationApi api = api(entryMetaUri);
    if (api != null) {
      return api.getEntry(entryMetaUri, objectUri);
    } else {
      log.warn(
          "Unable to find navigation api for the meta: " + entryMetaUri + " (" + objectUri + ")");
    }
    return null;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    apiByName = new HashMap<>();
    if (apis != null) {
      for (NavigationApi api : apis) {
        apiByName.put(api.name(), api);
      }
    }

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
  public Optional<ApiObjectRef> loadObject(URI entryMetaUri, URI objectUri) {
    NavigationApi api = api(entryMetaUri);
    if (api != null) {
      return api.loadObject(entryMetaUri, objectUri);
    } else {
      log.warn(
          "Unable to find navigation api for the meta: " + entryMetaUri + " (" + objectUri + ")");
    }
    return Optional.empty();
  }

  @Override
  public Navigation start(String navigationConfigName) {
    NavigationConfig navigationConfig = navConfigsByName.get(navigationConfigName);
    if (navigationConfig == null) {
      log.debug(
          "Unable to start {} navigation, missing configuration by name. Extend Configuration!",
          navigationConfigName);
      return null;
    }
    Navigation result = new Navigation(navigationConfig, this);
    return result;
  }

  @Override
  public Disposable subscribeEntryForChanges(NavigationEntry entry,
      Consumer<URI> nodeChangeListener) {
    NavigationApi api = api(entry.getMetaUri());
    if (api != null) {
      return api.subscribeEntryForChanges(entry, nodeChangeListener);
    } else {
      log.warn(
          "Unable to find navigation api for the meta: " + entry.getMetaUri() + " ("
              + entry.getObjectUri() + ")");
    }
    return null;
  }
}
