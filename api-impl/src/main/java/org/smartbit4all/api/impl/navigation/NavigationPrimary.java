package org.smartbit4all.api.impl.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public class NavigationPrimary extends NavigationImpl implements InitializingBean {

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
  public Map<NavigationAssociationMeta, List<NavigationReferenceEntry>> navigate(
      NavigationEntry entry, List<NavigationAssociationMeta> associations) {
    // In this case the scheme of the uri in the NavigationAssociationMeta identifies the api to
    // delegate.
    List<NavigationAssociationMeta> toNavigate = getAssociationsToNavigate(entry, associations);
    if (toNavigate == null || toNavigate.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<NavigationApi, List<NavigationAssociationMeta>> assocByApi = new HashMap<>();
    for (NavigationAssociationMeta associationMeta : toNavigate) {
      NavigationApi api = apiByName.get(associationMeta.getUri().getScheme());
      if (api != null) {
        List<NavigationAssociationMeta> assocList = assocByApi.get(api);
        if (assocList == null) {
          assocList = new ArrayList<>();
        }
        assocList.add(associationMeta);
        assocByApi.put(api, assocList);
      }
    }
    Map<NavigationAssociationMeta, List<NavigationReferenceEntry>> result = new HashMap<>();
    for (Entry<NavigationApi, List<NavigationAssociationMeta>> requestEntry : assocByApi
        .entrySet()) {
      // Call the given api with the list as parameter and merge the result.
      Map<NavigationAssociationMeta, List<NavigationReferenceEntry>> navigate =
          requestEntry.getKey().navigate(entry, requestEntry.getValue());
      result.putAll(navigate);
    }
    return result == null ? Collections.emptyMap() : result;
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
