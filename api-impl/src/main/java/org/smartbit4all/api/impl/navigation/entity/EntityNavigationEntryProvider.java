package org.smartbit4all.api.impl.navigation.entity;

import java.net.URI;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;

public interface EntityNavigationEntryProvider {

  /**
   * <b>Important:</b> The {@link NavigationEntryMeta} may not be set in the returned {@link NavigationEntry}!
   * @return The {@link NavigationEntry} for the given entity object without the meta set.
   */
  NavigationEntry getEntry(URI entityObjectUri);
  
}
