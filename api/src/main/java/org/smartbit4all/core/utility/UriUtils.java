/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UriUtils {

  private static final Logger log = LoggerFactory.getLogger(UriUtils.class);

  public static void checkURI(URI uriToCheck, String scheme, String host, String path) {
    try {
      checkURIPart(scheme, uriToCheck::getScheme);
      checkURIPart(host, uriToCheck::getHost);
      checkURIPart(path, uriToCheck::getPath);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e.getMessage() + " URI: " + uriToCheck.toString(), e);
    }
  }

  public static void checkURIPart(String partValue, Supplier<String> partSupplier)
      throws IllegalAccessException {
    if (partValue != null && !partValue.equals(partSupplier.get())) {
      throw new IllegalAccessException("The given uri's format is not acceptable!");
    }
  }

  public static URI removeFragment(URI uri) {
    return createUri(uri.getScheme(), uri.getHost(), uri.getPath(), null);
  }

  public static URI createUri(String scheme, String host, String path, String fragment) {
    try {
      return new URI(scheme, host, path, fragment);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static String formatUriHost(String source) {
    String formattedSource = source.replace(".", "-").replace("_", "-");
    if (!formattedSource.equals(source)) {
      log.info(
          "The given source can not be set to the uri in this form because of illegal characters.\n"
              + "\tGiven source: " + source + "\n"
              + "\tThe source has been updated: " + formattedSource);
    }
    return formattedSource;
  }

  public static String mapToQuery(Map<String, String> query) {
    String queryString = query.entrySet()
        .stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining("&"));
    return queryString;
  }

  public static Map<String, String> queryToMap(String queryString) {
    if (queryString == null || queryString.isEmpty()) {
      return new HashMap<>();
    }
    String[] entries = queryString.split("&");
    if (entries == null) {
      throw new RuntimeException("Unable to process query string: " + queryString);
    }
    if (entries.length == 0) {
      return new HashMap<>();
    }
    return Arrays.stream(entries)
        .collect(Collectors.toMap(e -> splitQueryEntry(e, true),
            e -> splitQueryEntry(e, false)));
  }

  public static String splitQueryEntry(String entry, boolean first) {
    String[] split = entry.split("=");
    if (split == null || split.length != 2) {
      throw new RuntimeException("Unable to process query entry: " + entry);
    }
    return split[first ? 0 : 1];
  }

  /**
   * @param uri
   * @param versionPostfix
   * @return
   */
  public static final URI getUriWithoutVersion(URI uri, String versionPostfix) {
    String path = uri.getPath();
    int idxVersionPostfix = path.lastIndexOf(versionPostfix);
    if (idxVersionPostfix >= 0) {
      return UriUtils.createUri(uri.getScheme(), null, path.substring(0, idxVersionPostfix), null);
    }
    return uri;
  }

}
