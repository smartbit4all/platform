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

public abstract class EntityUris {

  public static final String SCHEME_ENTITY = "entity";
  
  private EntityUris() {
  }
  
  public static URI createEntityUri(String source, String entityPath) {
    return createUri(SCHEME_ENTITY, source, entityPath, null);
  }
  
  public static URI createPropertyUri(String source, String entityPath, String property) {
    return createUri(SCHEME_ENTITY, source, entityPath, property);
  }
  
  public static String getSource(URI uri) {
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
