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
package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;

public class NavigationConfigBuilder {

  private NavigationConfig config = new NavigationConfig();

  private Map<URI, NavigationEntryMeta> entries = new HashMap<>();

  /**
   * Factory method for the builder.
   * 
   * @return
   */
  public static final NavigationConfigBuilder builder() {
    return new NavigationConfigBuilder();
  }

  public NavigationConfigBuilder contribute(NavigationAssociationMeta associationMeta) {
    // create a copy of the assoc for the configuration
    // create copies of entries as well keeping the connections with the added assoc metas
    NavigationAssociationMeta configAssocMeta = new NavigationAssociationMeta()
        .uri(associationMeta.getUri())
        .name(associationMeta.getName());
    NavigationEntryMeta startConfigEntry = registerEntry(associationMeta.getStartEntry());
    startConfigEntry.addAssociationsItem(configAssocMeta);
    configAssocMeta.setStartEntry(startConfigEntry);
    configAssocMeta.setEndEntry(registerEntry(associationMeta.getEndEntry()));
    if (associationMeta.getAssociationEntry() != null) {
      configAssocMeta.setAssociationEntry(registerEntry(associationMeta.getAssociationEntry()));
    }
    config.addAssociationsItem(configAssocMeta);
    return this;
  }

  public NavigationConfig build() {
    return config;
  }

  private NavigationEntryMeta registerEntry(NavigationEntryMeta entry) {
    NavigationEntryMeta entryMeta = entries.get(entry.getUri());
    if (entryMeta == null) {
      // creating copy from the entry
      entryMeta = new NavigationEntryMeta()
          .uri(entry.getUri())
          .name(entry.getName());
      entries.put(entry.getUri(), entryMeta);
      config.addEntriesItem(entryMeta);
    }
    return entryMeta;
  }

}
