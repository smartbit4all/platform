package org.smartbit4all.api.object;

import java.net.URI;

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

  ReferenceChange(URI parentObjectURI, String name, ObjectChange changedReference) {
    super(parentObjectURI, name);
    this.changedReference = changedReference;
  }

  /**
   * The change event of the reference.
   */
  public final ObjectChange getChangedReference() {
    return changedReference;
  }

}
