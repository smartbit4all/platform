package org.smartbit4all.api.mdm;

import java.net.URI;

public interface MDMSubscriberApi {

  /**
   *
   * @param event Event kind
   * @param definition URI of MDMDefinition
   * @param scope Scope of change. null for global, entry/group name for entry/group level events.
   * @param state URI of actual state
   * @param prevState URI of the previous state
   */
  void stateChanged(String event, String scope, URI definition, URI state, URI prevState);

}
