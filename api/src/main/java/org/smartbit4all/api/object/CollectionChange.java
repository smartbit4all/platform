package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The change of a collection with instance changes inside.
 * 
 * @author Peter Boros
 */
public class CollectionChange extends ChangeItem {

  /**
   * The instance changes for the given collection. New and deleted items will be included.
   */
  private final List<CollectionItemChange> changes = new ArrayList<>();

  CollectionChange(URI parentObjectURI, String name) {
    super(parentObjectURI, name);
  }

  /**
   * The instance changes of the given collection change.
   * 
   * @return
   */
  public final List<CollectionItemChange> getChanges() {
    return changes;
  }

}
