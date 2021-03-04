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
package org.smartbit4all.api.navigation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.navigation.NavigationConfig.ConfigBuilder;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This navigation can be used to collect and manage a fixed navigation tree. If we have this static
 * implementation then we can add static elements to the navigation. For the maximal
 * parameterization the static tree is a special {@link NavigationConfig} and every entry and
 * association have their own meta. So we can add meta info as well.
 * 
 * @author Peter Boros
 */
public class NavigationImplStatic extends NavigationImpl {


  private static final Logger log = LoggerFactory.getLogger(NavigationImplStatic.class);

  Map<URI, NavigationEntry> entries = new HashMap<>();

  /**
   * The reference maps of the entries by the entry URI.
   */
  Map<URI, Map<URI, List<NavigationReferenceEntry>>> entryReferenceMaps = new HashMap<>();

  Map<URI, NavigationAssociationMeta> assocMetas = new HashMap<>();

  Map<URI, NavigationEntryMeta> entryMetas = new HashMap<>();

  public NavigationImplStatic(String name) {
    super(name);
  }

  private final URI entryUri(URI parentUri, String assocName, String entryName) {
    try {
      return new URI(name, null,
          parentUri.getPath() + StringConstant.SLASH + assocName + StringConstant.SLASH + entryName,
          null);
    } catch (URISyntaxException e) {
      log.error("Invalid uri during bilding the " + name + " static navigation (" + parentUri + ", "
          + assocName + ", " + entryName + ")", e);
    }
    return null;
  }

  private final URI assocUri(URI parentUri, String assocName) {
    try {
      return new URI(name, null, parentUri.getPath() + StringConstant.SLASH + assocName, null);
    } catch (URISyntaxException e) {
      log.error("Invalid uri during bilding the " + name + " static navigation (" + parentUri + ", "
          + assocName + ")", e);
    }
    return null;
  }

  private final URI rootUri(String entryName) {
    try {
      return new URI(name, null,
          StringConstant.SLASH + entryName,
          null);
    } catch (URISyntaxException e) {
      log.error("Invalid uri during bilding the " + name + " static navigation (" + entryName + ")",
          e);
    }
    return null;
  }

  public URI addRoot(String name, String title, String icon, NavigationView... views) {
    // find the meta first.
    URI uri = rootUri(name);
    NavigationEntryMeta navigationEntryMeta =
        entryMetas.computeIfAbsent(uri, u -> Navigation.entryMeta(u, name));
    entries.put(uri, Navigation.entry(navigationEntryMeta, uri, title, icon, views));
    return uri;
  }

  public URI addChild(URI parentUri, String assocName, String name, String title, String icon,
      NavigationView... views) {
    // Find the parent first. Get it's metas and append new association if necessary.
    NavigationEntry parentEntry = entries.get(parentUri);
    NavigationEntryMeta parentEntryMeta = entryMetas.get(parentUri);
    if (parentEntry != null) {
      URI uri = entryUri(parentUri, assocName, name);
      NavigationEntryMeta navigationEntryMeta =
          entryMetas.computeIfAbsent(uri, u -> Navigation.entryMeta(u, name));
      URI assocUri = assocUri(parentUri, assocName);
      NavigationAssociationMeta associationMeta =
          assocMetas.computeIfAbsent(assocUri, u -> Navigation.assocMeta(assocUri, assocName,
              parentEntryMeta, navigationEntryMeta, null));
      parentEntryMeta.addAssociationsItem(associationMeta);
      NavigationEntry entry = Navigation.entry(navigationEntryMeta, uri, title, icon, views);
      entries.put(uri, entry);
      Map<URI, List<NavigationReferenceEntry>> entryReferenceMap =
          entryReferenceMaps.computeIfAbsent(parentUri, u -> new HashMap<>());
      List<NavigationReferenceEntry> references =
          entryReferenceMap.computeIfAbsent(assocUri, u -> new ArrayList<>());
      references.add(Navigation.referenceEntry(parentUri, entry, null));
      return uri;
    }
    return null;
  }

  public void addLeaf(URI parentUri, String assocName, URI objectUri, String title, String icon,
      NavigationView... views) {
    NavigationEntry parentEntry = entries.get(parentUri);
    NavigationEntryMeta parentEntryMeta = entryMetas.get(parentUri);
    if (parentEntry != null) {
      NavigationEntryMeta navigationEntryMeta =
          entryMetas.computeIfAbsent(objectUri, u -> Navigation.entryMeta(u, name));
      URI assocUri = assocUri(parentUri, assocName);
      NavigationAssociationMeta associationMeta =
          assocMetas.computeIfAbsent(assocUri, u -> Navigation.assocMeta(assocUri, assocName,
              parentEntryMeta, navigationEntryMeta, null));
      parentEntryMeta.addAssociationsItem(associationMeta);
      NavigationEntry entry = Navigation.entry(navigationEntryMeta, objectUri, title, icon, views);
      entries.put(objectUri, entry);
      Map<URI, List<NavigationReferenceEntry>> entryReferenceMap =
          entryReferenceMaps.computeIfAbsent(parentUri, u -> new HashMap<>());
      List<NavigationReferenceEntry> references =
          entryReferenceMap.computeIfAbsent(assocUri, u -> new ArrayList<>());
      references.add(Navigation.referenceEntry(parentUri, entry, null));
    }
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris) {
    Map<URI, List<NavigationReferenceEntry>> result = new HashMap<>();
    Map<URI, List<NavigationReferenceEntry>> referenceMaps = entryReferenceMaps.get(objectUri);
    for (URI uri : associationMetaUris) {
      result.put(uri, referenceMaps.get(uri));
    }
    return result;
  }

  @Override
  public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) {
    return entries.get(objectUri);
  }

  /**
   * The navigation configuration of the static navigation tree to contribute in a navigation.
   * 
   * @return
   */
  public NavigationConfig config() {
    ConfigBuilder builder = NavigationConfig.builder();
    append(builder);
    return builder.build();
  }

  public void append(ConfigBuilder builder) {
    assocMetas.values().forEach(builder::addAssociationMeta);
  }

}
