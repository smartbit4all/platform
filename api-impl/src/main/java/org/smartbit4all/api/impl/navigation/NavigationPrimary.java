package org.smartbit4all.api.impl.navigation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationImpl;
import org.smartbit4all.api.navigation.NavigationURI;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This implementation of the navigation api coordinates among the registered
 * {@link NavigationApi}-s. All of them are mapped here with their names and the requests are
 * managed by the uri namespaces.
 * 
 * @author Peter Boros
 */
public class NavigationPrimary extends NavigationImpl implements InitializingBean {

  public static final String PRIMARY = "NavigationPrimary";

  /**
   * This list is autowired with the registered {@link NavigationApi}s from the context.
   */
  @Autowired
  List<NavigationApi> apis;

  /**
   * The api instances by their name.
   */
  Map<String, NavigationApi> apiByName = new HashMap<>();

  public NavigationPrimary() {
    super(PRIMARY);
  }

  /**
   * In this post construct operation we create a map of the registered {@link NavigationApi}s.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    for (NavigationApi api : apis) {
      apiByName.put(api.name(), api);
    }
  }

  @Override
  public NavigationEntry get(NavigationURI uri) {
    NavigationApi api = apiByName.get(uri.getNavigation());
    return api != null ? api.get(uri) : null;
  }

  @Override
  public List<NavigationEntry> children(NavigationURI uri) {
    NavigationApi api = apiByName.get(uri.getNavigation());
    return api != null ? api.children(uri) : Collections.emptyList();
  }

  @Override
  public NavigationEntry parent(NavigationURI uri) {
    NavigationApi api = apiByName.get(uri.getNavigation());
    return api != null ? api.parent(uri) : null;
  }

}
