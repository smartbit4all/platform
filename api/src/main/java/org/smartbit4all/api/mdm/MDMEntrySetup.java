package org.smartbit4all.api.mdm;

import java.util.List;
import java.util.Map;

/**
 * This setup interface should be implemented by the Apis responsible for managing one or more MDM
 * entries. Like an api that register all the implementations of some sort of services. In this case
 * the {@link MasterDataManagementApi} itself is about to call this to setup the given entry.
 * 
 * @author Peter Boros
 */
public interface MDMEntrySetup {

  /**
   * @return The api must return the definition to setup. If this definition is missing then the
   *         setupEntries is not called.
   */
  String getDefinitionToSetup();

  /**
   * @return The entry names to setup.
   */
  List<String> getEntriesToSetup();

  /**
   * The setup function called by the {@link MasterDataManagementApi} when it time to setup before
   * start the usage.
   * 
   * @param entries
   */
  void setupEntries(Map<String, MDMEntryApi> entries);

}
