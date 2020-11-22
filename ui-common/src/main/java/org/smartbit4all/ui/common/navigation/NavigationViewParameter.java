package org.smartbit4all.ui.common.navigation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * The generic parameter bean for {@link NavigationView} implementations. They can autowire this
 * parameter since this is a thread local scoped bean. The controller can initiate this bean and
 * setup before opening the view. The view instance itself will receive the same instance because it
 * will be opened in the same thread.
 * 
 * @author Peter Boros
 */
public class NavigationViewParameter {

  private URI entryUri;

  Map<String, Object> parameters = new HashMap<>();

  public final URI getentryUri() {
    return entryUri;
  }

  public final NavigationViewParameter setEntryUri(URI entryUri) {
    this.entryUri = entryUri;
    return this;
  }

  public NavigationViewParameter set(String name, Object value) {
    parameters.put(name, value);
    return this;
  }

  public Object get(String name) {
    return parameters.get(name);
  }

}
