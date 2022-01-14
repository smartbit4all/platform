package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The one and only implementation of the feature api. Always configured and can be autowired to
 * access the available apis for the navigation.
 * 
 * @author Peter Boros
 */
public final class NavigationFeatureApiImpl implements NavigationFeatureApi, InitializingBean {

  /**
   * The central register of the apis registered for a given meta uri.
   */
  private final Map<URI, Map<Class<?>, List<Object>>> featuresMap = new HashMap<>();

  /**
   * All the add ons available in the
   */
  @Autowired(required = false)
  private List<NavigationFeatureAddOn> addOns;

  @SuppressWarnings("unchecked")
  @Override
  public <A> List<A> api(Class<A> apiClass, URI meta) {
    if (meta == null) {
      return Collections.emptyList();
    }
    Map<Class<?>, List<Object>> entryApis = featuresMap.get(meta);
    if (entryApis == null) {
      return Collections.emptyList();
    }
    List<Object> apis = entryApis.get(apiClass);
    return apis != null ? (List<A>) Collections.unmodifiableList(apis) : Collections.emptyList();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (addOns != null) {
      for (NavigationFeatureAddOn addOn : addOns) {
        Map<URI, Map<Class<?>, Object>> providedApis = addOn.providedApis();
        if (providedApis != null) {
          for (Entry<URI, Map<Class<?>, Object>> entryAddOns : providedApis
              .entrySet()) {
            Map<Class<?>, List<Object>> apisByClass =
                featuresMap.computeIfAbsent(entryAddOns.getKey(), u -> new HashMap<>());
            for (Entry<Class<?>, Object> entryApi : entryAddOns.getValue().entrySet()) {
              apisByClass.computeIfAbsent(entryApi.getKey(), c -> new ArrayList<>())
                  .add(entryApi.getValue());
            }
          }
        }
      }
    }
  }

}
