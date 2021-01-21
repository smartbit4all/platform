package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

  CollectionChange(UUID parentId, String name) {
    super(parentId, name);
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
