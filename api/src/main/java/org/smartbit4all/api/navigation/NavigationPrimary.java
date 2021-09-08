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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

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
      List<URI> associationMetaUris) {
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
          requestEntry.getKey().navigate(objectUri, requestEntry.getValue());
      result.putAll(navigate);
    }
    return result;
  }

  private NavigationApi api(URI associationUri) {
    return apiByName.get(associationUri.getScheme());
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
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris, NavigationCallBackApi callBack) {
    return navigate(objectUri, associationMetaUris);
  }

}
