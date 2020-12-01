package org.smartbit4all.api.impl.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationImpl;
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
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI entryUri, List<URI> associations) {
    // In this case the scheme of the uri in the NavigationAssociationMeta identifies the api to
    // delegate.
    if (associations == null || associations.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<NavigationApi, List<URI>> assocByApi = new HashMap<>();
    for (URI associationUri : associations) {
      NavigationApi api = api(associationUri);
      if (api != null) {
        List<URI> assocList = assocByApi.get(api);
        if (assocList == null) {
          assocList = new ArrayList<>();
        }
        assocList.add(associationUri);
        assocByApi.put(api, assocList);
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
          requestEntry.getKey().navigate(entryUri, requestEntry.getValue());
      result.putAll(navigate);
    }
    return result == null ? Collections.emptyMap() : result;
  }

  private NavigationApi api(URI associationUri) {
    NavigationApi api = apiByName.get(associationUri.getScheme());
    return api;
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

}
