package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class EntityUris {

  public static final String SCHEME_ENTITY = "entity";
  
  private EntityUris() {
  }
  
  public static URI createEntityUri(String domain, String entityPath) {
    return createUri(SCHEME_ENTITY, domain, entityPath, null);
  }
  
  public static URI createPropertyUri(String domain, String entityPath, String property) {
    return createUri(SCHEME_ENTITY, domain, entityPath, property);
  }
  
  public static String getDomain(URI uri) {
    checkScheme(uri);
    return uri.getAuthority();
  }
  
  public static String getEntityPath(URI uri) {
    checkScheme(uri);
    String path = uri.getPath();
    path = path == null ? null : path.replaceFirst("/", "");
    return path;
  }
  
  public static String getProperty(URI uri) {
    checkScheme(uri);
    return uri.getFragment();
  }
  
  private static void checkScheme(URI uri) {
    if (!SCHEME_ENTITY.equals(uri.getScheme())) {
      throw new IllegalArgumentException(
          "The given URI is not acceptable as entity uri due not matching scheme.");
    }
  }
  
  private static URI createUri(String scheme, String host, String path, String fragment) {
    path = path == null ? null : path.startsWith("/") ? path : "/" + path;
    try {
      return new URI(scheme, host, path, fragment);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
  
}
