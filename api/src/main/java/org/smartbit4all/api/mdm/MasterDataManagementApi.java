package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMErrorLog;
import org.smartbit4all.api.mdm.bean.MDMModificationRequest;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.core.utility.StringConstant;

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

  /**
   * The default global definition name.
   */
  static final String MDM_DEFINITION_GLOBAL = "globalMdmDefinition";

  /**
   * The definition name of the system definition.
   */
  static final String MDM_DEFINITION_SYSTEM_INTEGRATION = "systemIntegration";

  /** The canonical name of this API, used by listeners to identify this service. */
  static final String API = "org.smartbit4all.api.mdm.MasterDataManagementApi";

  /** Generic state change event */
  static final String STATE_CHANGED = "state_changed";

  static final String MODIFICATION_STARTED = "modification_started";

  static final String MODIFICATION_CANCELLED = "modification_cancelled";

  static final String MODIFICATION_FINALIZED = "modification_finalized";

  static final String MODIFICATION_SENT_FOR_APPROVAL = "modification_sent_for_approval";

  static final String MODIFICATION_APPROVED = "modification_approved";

  static final String MODIFICATION_REJECTED = "modification_rejected";

  static final String SCHEMA = "mdm";

  static final String PATH_SEPARATOR = StringConstant.SLASH;

  /**
   * Return the api responsible for the management if the given {@link MDMEntryDescriptor}.
   *
   * @param definition The definition name.
   * @param name The name of the descriptor
   * @return The prepared api.
   * @exception IllegalArgumentException It throws exception if the given definition or entry is not
   *            found.
   */
  MDMEntryApi getApi(String definition, String name, URI branch);

  default MDMEntryApi getApi(String definition, String name) {
    return getApi(definition, name, null);
  }

  /**
   * Return the api responsible for the management if the given {@link MDMEntryDescriptor}.
   *
   * @param definition The definition name.
   * @param name The name of the descriptor
   * @return The prepared api or null if the definition or the entry is not found. Doesn't throw
   *         exception on missing entry.
   */
  MDMEntryApi getApiSafe(String definition, String name);

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
  MDMEntryDescriptor getEntryDescriptor(MDMDefinition definition, String entryName, URI branch);

  default MDMEntryDescriptor getEntryDescriptor(MDMDefinition definition, String entryName) {
    return getEntryDescriptor(definition, entryName, null);
  }

  /**
   * Retrieve the entry descriptor from a MDM definition.
   *
   * @param definitionName The {@link MDMDefinition} name.
   * @param entryName The name of the entry.
   * @return The descriptor object. Please do not modify this object.
   */
  MDMEntryDescriptor getEntryDescriptor(String definitionName, String entryName, URI branch);

  default MDMEntryDescriptor getEntryDescriptor(String definitionName, String entryName) {
    return getEntryDescriptor(definitionName, entryName, null);
  }

  /**
   * Retrieve the entry descriptors from a MDM definition on a given branch.
   *
   * @param definition The {@link MDMDefinition} itself.
   * @param branch URI of branch (currently only globalBranch is supported)
   * @return The map of descriptors. Please do not modify this object.
   */
  Map<String, MDMEntryDescriptor> getEntryDescriptors(MDMDefinition definition, URI branch);

  String constructObjectDefinitionName(MDMDefinition definition, MDMEntryDescriptor descriptor);

  URI initiateGlobalBranch(String definition, String title);

  URI getGlobalBranch(String definition);

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
  URI mergeGlobal(String definitionName);

  /**
   * The current editing branch will be finished and the changes won't be merged into the main
   * branch.
   *
   * @param definitionName The name of the definition.
   *
   */
  URI dropGlobal(String definitionName);

  void sendForApprovalGlobal(String definitionName, URI approver);

  void approvalAcceptedGlobal(String definitionName);

  void approvalRejectedGlobal(String definitionName, String reason);

  URI addNewEntries(MDMDefinitionOption o, URI branch);

  void saveVectorCollectionDescriptor(String definitionName, String entryName,
      VectorCollectionDescriptor vectorCollectionDescriptor);

  void modifyEntry(String definitionName, MDMEntryDescriptor entry, URI branch);

  /**
   *
   * Updates the definition with the InvocationRequest stored inside of it.
   *
   * @param definitionName The name of the definition.
   *
   */
  void executeMdmDefinitionUpdate(String definitionName);

  MDMErrorLog importData(String definitionName, String entryName,
      MDMModificationRequest modificationRequest);

}
