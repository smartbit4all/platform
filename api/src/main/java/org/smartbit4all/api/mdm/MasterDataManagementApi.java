package org.smartbit4all.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;

/**
 * The global master data management api that is responsible for the master data of the application.
 * It is working with the {@link MDMDefinition} object that describes the master data management
 * schemas of the application. We can define the MDMDefinition and its details in spring context. In
 * this case the {@link MasterDataManagementApi} will apply the setting into the storage at the
 * first call. It is a fast operation and the operation state is stored also in the storage.
 * 
 * @author Peter Boros
 */
public interface MasterDataManagementApi {

  static final String SCHEMA = "mdm";

  /**
   * Return the api responsible for the management if the given {@link MDMEntryDescriptor}.
   * 
   * @param definition The definition name.
   * @param name The name of the descriptor
   * @return The prepared api.
   */
  MDMEntryApi getApi(String definition, String name);

  /**
   * Retrieve the MDM definition.
   * 
   * @param definition The name of the definition.
   * @return The definition object. Please do not modify this object.
   */
  MDMDefinition getDefinition(String definition);

  /**
   * Retrieve the entry descriptor from a MDM definition.
   * 
   * @param definition The {@link MDMDefinition} itself.
   * @param entryName The name of the entry.
   * @return The descriptor object. Please do not modify this object.
   */
  MDMEntryDescriptor getEntryDescriptor(MDMDefinition definition, String entryName);

}
