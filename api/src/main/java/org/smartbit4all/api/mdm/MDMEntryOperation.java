package org.smartbit4all.api.mdm;

/**
 * The operation modes for the {@link MDMEntryApi#save(java.util.List)}.
 */
public enum MDMEntryOperation {

  /**
   * The given nodes are saved by their URI as is. The new will be new, the update will be update.
   * It's up to the caller to have a correct list of nodes to save. This is the default operation.
   */
  SAVE,
  /**
   * Try to find the given nodes by the unique identifier and ignore the URI of the ObjectNode
   * itself. If a node is not found then it will be a new object. If it exists then we update the
   * existing object.
   */
  UPDATE,

  /**
   * The same as the {@link #UPDATE} but if some existing nodes are missing from the list then we
   * remove them from the list.
   */
  SET

}
