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
