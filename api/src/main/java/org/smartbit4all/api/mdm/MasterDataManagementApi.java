package org.smartbit4all.api.mdm;

import java.net.URI;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;

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

  String constructObjectDefinitionName(MDMDefinition definition, MDMEntryDescriptor descriptor);

  URI initiateGlobalBranch(String definition, String title);

  URI initiateBranchForEntry(String definition, String title, String entryName);

  /**
   * The current editing branch will be merged into be main branch. From that moment the
   * getBranchingList() return a {@link BranchedObjectEntry} list with
   * {@link BranchingStateEnum#NOP} for all object. The previous local editing branch is finished
   * and removed from the {@link BranchEntry}.
   * 
   * @param definitionName The name of the definition.
   * 
   */
  void mergeGlobal(String definitionName);

  /**
   * The current editing branch will be finished and the changes won't be merged into the main
   * branch.
   * 
   * @param definitionName The name of the definition.
   * 
   */
  void dropGlobal(String definitionName);

}
