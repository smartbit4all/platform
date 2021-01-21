package org.smartbit4all.api.object;

import java.util.UUID;

/**
 * The specific version of property change. In this case we have reference points to another object
 * that is part of the object hierarchy and managed.
 * 
 * @author Peter Boros
 */
public class ReferenceChange extends ChangeItem {

  /**
   * The change event of the reference.
   */
  private final ObjectChange changedReference;

  ReferenceChange(UUID parentId, String name, ObjectChange changedReference) {
    super(parentId, name);
    this.changedReference = changedReference;
  }

  /**
   * The change event of the reference.
   */
  public final ObjectChange getChangedReference() {
    return changedReference;
  }

}
