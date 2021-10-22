package org.smartbit4all.domain.data.storage;

import java.util.HashMap;
import java.util.function.Supplier;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectChange;

/**
 * This event object that is created by the implementation of the {@link ObjectStorage}.
 * 
 * @author Peter Boros
 *
 * @param <O> The object that is the value in the save event.
 */
public final class StorageSaveEvent<O> {

  /**
   * A mandatory supplier to produce the old value of the given save event.
   */
  private Supplier<O> oldValueSupplier;

  public StorageSaveEvent(Supplier<O> oldValueSupplier, O newVersion) {
    super();
    this.oldValueSupplier = oldValueSupplier;
    this.newVersion = newVersion;
  }

  /**
   * The old version of the object
   */
  private O oldVersion;

  private O newVersion;

  private ObjectChange change;

  public final O getOldVersion() {
    if (oldVersion == null) {
      oldVersion = oldValueSupplier.get();
    }
    return oldVersion;
  }

  public final O getNewVersion() {
    return newVersion;
  }

  public final ObjectChange getChange() {
    if (change == null) {
      O myOldVersion = getOldVersion();
      O myNewVersion = getNewVersion();
      ApiObjectRef ref = new ApiObjectRef(null, myOldVersion, new HashMap<>());
      ref.mergeObject(myNewVersion);
      change = ref.renderAndCleanChanges().orElse(ObjectChange.EMPTY);
    }
    return change;
  }

}
