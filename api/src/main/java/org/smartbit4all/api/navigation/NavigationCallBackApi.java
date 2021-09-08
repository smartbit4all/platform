package org.smartbit4all.api.navigation;

import java.net.URI;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;

/**
 * The navigation as an instance provides a callback api or the contribution apis. This callback is
 * registered via {@link InvocationRequestTemplate} and triggered if the source data has been
 * changed.
 * 
 * @author Peter Boros
 */
@FunctionalInterface
public interface NavigationCallBackApi {

  /**
   * This callback method will discover if the given object is currently involved in the navigation.
   * If it's so then it will refresh the given object itself and its children. This refresh is not
   * recursive.
   * 
   * @param objectUri
   */
  void nodeChanged(URI objectUri);

}
