package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class EntityUris {

  private static final String ENTITYOBJECT_ID = "id";
  public static final String SCHEME_ENTITY = "entity";
  
  private EntityUris() {
  }
  
  public static URI createEntityUri(String domain, String entityPath) {
    return createUri(SCHEME_ENTITY, domain, entityPath, null);
  }
  
  public static URI createPropertyUri(String domain, String entityPath, String property) {
    return createUri(SCHEME_ENTITY, domain, entityPath, property);
  }
  
  public static URI createReferenceUri(String domain, String entityName, String referenceName) {
    return createUri(SCHEME_ENTITY, domain, entityName + "/" + referenceName, null);
  }
  
  public static URI createDetailUri(String domain, String mainEntityName, String detailEntityName) {
    return createUri(SCHEME_ENTITY, domain, mainEntityName + "/" + detailEntityName, null);
  }
  
  // TODO method for id set
  public static URI createEntityObjectUri(URI entityUri, String id) {
    String domain = getDomain(entityUri);
    String entityPath = getEntityPath(entityUri);
    Map<String, String> query = new HashMap<>();
    query.put(ENTITYOBJECT_ID, id);
    return createUri(SCHEME_ENTITY, domain, entityPath, query, null);
  }
  
  public static String getEntityObjectId(URI entityObjectUri) {
    return getQueryParam(entityObjectUri, ENTITYOBJECT_ID);
  }
  
  public static String getQueryParam(URI uri, String key) {
    checkScheme(uri);
    String queryString = uri.getQuery();
    Map<String, String> query = queryToMap(queryString);
    return query.get(key);
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
  
  public static String getEntityName(URI uri) {
    String path = getEntityPath(uri);
    int slashIdx = path.indexOf("/");
    if(slashIdx != -1) {
      String  entityName = path.substring(0, slashIdx);
      return entityName;
    } else {
      return path;
    }
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
    return createUri(scheme, host, path, null, fragment);
  }
  
  private static URI createUri(String scheme, String host, String path, Map<String, String> query,
      String fragment) {
    path = path == null ? null : path.startsWith("/") ? path : "/" + path;
    String queryString = query == null ? null : mapToQuery(query); 
    try {
      return new URI(scheme, host, path, queryString, fragment);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static String mapToQuery(Map<String, String> query) {
    String queryString = query.entrySet()
        .stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining("&"));
    return queryString;
  }
  
  private static Map<String, String> queryToMap(String queryString) {
    String[] entries = queryString.split("&");
    if(entries == null) {
      throw new RuntimeException("Unable to process query string: " + queryString);
    }
    return Arrays.stream(entries)
        .collect(Collectors.toMap(e -> splitQueryEntry(e, true),
                                  e -> splitQueryEntry(e, false)));
  }
  
  private static String splitQueryEntry(String entry, boolean first) {
    String[] split = entry.split("=");
    if(split == null || split.length != 2) {
      throw new RuntimeException("Unable to process query entry: " + entry);
    }
    return split[first ? 0 : 1];
  }
  
}
