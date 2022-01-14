package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;

/**
 * The navigation is only the structure of the data. Different APIs can provide services and
 * operations attached to the navigation entries. The extension of the navigation structure can be
 * structural. We can attach more and more associations and entries. But on the other hand we can
 * add functionalities to these entries by adding
 * 
 * This primary api collects the contributions that are offering "features" as api interfaces. One
 * feature can be the viewer UI itself because to open up the UI we can call an API. The feature is
 * a set of API, the feature has a name and exactly identify the apis. This primary api provides the
 * apis by class or by name and the additional parameter is the navigation entry.
 * 
 * Even the associations can be contributed to have a contribution that is sophisticated enough to
 * parameterize all the toolbar buttons and support the recursive algorithms based on the navigation
 * structure.
 * 
 * @author Peter Boros
 */
public interface NavigationFeatureApi {

  /**
   * Using this we can get a specific api for the given navigation entry meta. It can be a folder
   * for example and the read folder content is contributed api. If we navigating the objects we
   * could need this api. If we are sure that this api is a singleton. There can be only one
   * contribution then we can use this function to access this api and we can call it later on.
   * 
   * @param <A> Typed by the class of the requested api.
   * @param apiClass The class of the requested api.
   * @param entryMeta The meta object of the navigation entry.
   * @return The api if exists.
   */
  <A> List<A> api(Class<A> apiClass, URI meta);

}
