/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.entity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class EntityUris {

  private static final String QUERY_ENTITYOBJECT_ID = "id";
  private static final String QUERY_PROPERTY_FUNCTION = "function";
  public static final String SCHEME_ENTITY = "entity";
  
  private EntityUris() {
  }
  
  public static URI createEntityUri(String domain, String entityPath) {
    return createUri(SCHEME_ENTITY, domain, entityPath, null);
  }
  
  public static URI createPropertyUri(String domain, String entityName, String property) {
    return createUri(SCHEME_ENTITY, domain, entityName, property);
  }
  
  public static URI createPropertyUri(String domain, String entityName, String refPath, String property) {
    return createUri(SCHEME_ENTITY, domain, entityName, refPath + "." + property);
  }
  
  public static URI createReferenceUri(String domain, String entityName, String referenceName) {
    return createUri(SCHEME_ENTITY, domain, entityName + "/" + referenceName, null);
  }
  
  public static URI createDetailUri(String domain, String mainEntityName, String detailEntityName) {
    return createUri(SCHEME_ENTITY, domain, mainEntityName + "/" + detailEntityName, null);
  }
  
  public static URI createFunctionPropertyUri(URI propertyUri, String functionName) {
    String domain = getDomain(propertyUri);
    String entityPath = getEntityPath(propertyUri);
    String propertyName = getProperty(propertyUri);
    Map<String, String> query = new HashMap<>();
    query.put(QUERY_PROPERTY_FUNCTION, functionName);
    return createUri(SCHEME_ENTITY, domain, entityPath, query, propertyName);
  }
  
  // TODO method for id set
  public static URI createEntityObjectUri(URI entityUri, String id) {
    String domain = getDomain(entityUri);
    String entityPath = getEntityPath(entityUri);
    Map<String, String> query = new HashMap<>();
    query.put(QUERY_ENTITYOBJECT_ID, id);
    return createUri(SCHEME_ENTITY, domain, entityPath, query, null);
  }
  
  public static boolean isEntityUri(URI uriToCheck) {
    if (SCHEME_ENTITY.equals(uriToCheck.getScheme())) {
      return true;
    }
    return false;
  }
  
  public static String getEntityObjectId(URI entityObjectUri) {
    return getQueryParam(entityObjectUri, QUERY_ENTITYOBJECT_ID);
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
  
  public static String getFunctionName(URI uri) {
    checkScheme(uri);
    return getQueryParam(uri, QUERY_PROPERTY_FUNCTION);
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
    if(queryString == null || queryString.isEmpty()) {
      return Collections.emptyMap();
    }
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
