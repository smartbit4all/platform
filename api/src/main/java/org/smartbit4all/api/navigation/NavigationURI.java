package org.smartbit4all.api.navigation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Utility class for the navigation URI. It can be constructed from URI or from parts of the
 * URI.
 * 
 * @author Peter Boros
 */
public final class NavigationURI {

  public static final String DELIMITER = "/";

  public static final String NAVIGATION_SEPARATOR = "://";

  private static final String UNKNOWN = "UNKNOWN";

  /**
   * The name of the navigation.
   */
  private String navigation;

  /**
   * The path inside the given navigation. It contains the identifier of the nodes till we reach the
   * current node. The last item of the path is typically the identifier of the current node.
   */
  private List<String> path;

  /**
   * The full URI with the following pattern:
   * 
   * navigation://navigationPath
   * 
   */
  private String uri;

  /**
   * Construct the URI object from the navigation and the path. The URI will be constructed.
   * 
   * @param navigation
   * @param path
   */
  public NavigationURI(String navigation, List<String> path) {
    super();
    this.navigation = navigation;
    this.path = path;
    constructURI();
  }

  /**
   * Construct the URI object from URI string. The navigation and the path will be calculated from
   * the URI.
   * 
   * @param uri
   */
  public NavigationURI(String uri) {
    super();
    this.uri = uri;
    parseURI();
  }

  /**
   * From the parts the uri will be constructed again.
   */
  private final void constructURI() {
    uri = (navigation == null ? UNKNOWN : navigation) + NAVIGATION_SEPARATOR
        + String.join(DELIMITER, path);
  }

  private final void parseURI() {
    if (uri != null) {
      int indexOf = uri.indexOf(NAVIGATION_SEPARATOR);
      if (indexOf >= 0) {
        navigation = uri.substring(0, indexOf);
        String fullPath = uri.substring(indexOf + NAVIGATION_SEPARATOR.length());
        path = Arrays.asList(fullPath.split(DELIMITER));
      } else {
        emptyURI();
      }
    } else {
      emptyURI();
    }
  }

  private void emptyURI() {
    uri = UNKNOWN + NAVIGATION_SEPARATOR;
    navigation = null;
    path = Collections.emptyList();
  }

  public final String getNavigation() {
    return navigation;
  }

  public final void setNavigation(String navigation) {
    this.navigation = navigation;
    constructURI();
  }

  public final List<String> getPath() {
    return path;
  }

  public final void setPath(List<String> path) {
    this.path = path;
    constructURI();
  }

  public final String getUri() {
    return uri;
  }

  public final void setUri(String uri) {
    this.uri = uri;
    parseURI();
  }

}
