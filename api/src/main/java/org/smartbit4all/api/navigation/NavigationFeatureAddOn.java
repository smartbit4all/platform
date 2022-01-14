package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.Map;

/**
 * This is the basic extension mechanism for the navigation. It can offer the functionality of
 * different apis to mimic a functionality for the navigation. At the end of the day the functions
 * is assembled into an api that is defined previously. If we would like to support the document
 * generation we can register the DocumentGenerationApi to every entry in the hierarchy. On the top
 * of the hierarchy we will have a specific entry that initiate the algorithm and start an ordered
 * traversal. If a node is found with available DocumentGenerationAPi then it will be included. This
 * is not an active item used only by the {@link NavigationFeatureApi} to discover the available
 * functionalities.
 * 
 * @author Peter Boros
 */
public interface NavigationFeatureAddOn {

  /**
   * The provided apis by the navigation entries. A map belong to every supported entry with the api
   * objects by the class of the api.
   * 
   * @return
   */
  Map<URI, Map<Class<?>, Object>> providedApis();

}
