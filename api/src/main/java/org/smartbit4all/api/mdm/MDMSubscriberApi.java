package org.smartbit4all.api.mdm;

import java.net.URI;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;

public interface MDMSubscriberApi {

  /**
   *
   * @param event Event kind
   * @param definition URI of MDMDefinition
   * @param scope Scope of change. null for global, entry/group name for entry/group level events.
   * @param state URI of actual state
   * @param prevState URI of the previous state
   * @param branchUri URI of the branch, where state is changed (identifies MDMModification on
   *        state)
   */
  void stateChanged(String event, String scope, URI definition, URI state, URI prevState,
      URI branchUri);

  /**
   * @param definition URI of MDMDefinition
   * @param entryDescriptorName Name of the {@link MDMEntryDescriptor}
   * @param objectUri The inactivated entry uri.
   * @param branchUri URI of the branch where the entry was inactivated.
   */
  void entryInactivated(URI definition, String entryDescriptorName, URI objectUri, URI branchUri);

  /**
   * @param definition URI of MDMDefinition
   * @param entryDescriptorName Name of the {@link MDMEntryDescriptor}
   * @param objectUri The removed entry uri.
   * @param branchUri URI of the branch where the entry was removed.
   */
  void entryRemoved(URI definition, String entryDescriptorName, URI objectUri, URI branchUri);
}
