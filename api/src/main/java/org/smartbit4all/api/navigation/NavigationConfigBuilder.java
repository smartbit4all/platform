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
    // Check the entries. If they are available in the current config then we use them instead of
    // the new instance.
    config.addAssociationsItem(associationMeta);
    associationMeta.setStartEntry(registerEntry(associationMeta.getStartEntry()));
    associationMeta.setStartEntry(registerEntry(associationMeta.getEndEntry()));
    if (associationMeta.getAssociationEntry() != null) {
      associationMeta.setAssociationEntry(registerEntry(associationMeta.getAssociationEntry()));
    }
    return this;
  }

  public NavigationConfig build() {
    return config;
  }

  private NavigationEntryMeta registerEntry(NavigationEntryMeta startEntry) {
    NavigationEntryMeta entryMeta = entries.get(startEntry.getUri());
    if (entryMeta == null) {
      entries.put(startEntry.getUri(), startEntry);
      entryMeta = startEntry;
      config.addEntriesItem(entryMeta);
    }
    return entryMeta;
  }

}
