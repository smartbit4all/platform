package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.HashMap;
import java.util.function.Supplier;
import org.smartbit4all.api.storage.bean.StorageSaveEventObject;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectChange;

/**
 * This event object that is created by the implementation of the {@link ObjectStorage}.
 * 
 * @author Peter Boros
 */
public final class StorageSaveEvent {

  /**
   * A mandatory supplier to produce the old value of the given save event.
   */
  private Supplier<?> oldVersionSupplier;

  private Supplier<URI> oldVersionUriSupplier;

  private Class<?> type;

  /**
   * The old version of the object
   */
  private Object oldVersion;

  private URI oldVersionUri;

  private Object newVersion;

  private URI newVersionUri;

  private ObjectChange change;

  private StorageSaveEvent() {}

  public StorageSaveEvent(Supplier<URI> oldValueUriSupplier, Supplier<?> oldValueSupplier,
      URI newVersionUri, Object newVersion, Class<?> type) {
    super();
    this.oldVersionSupplier = oldValueSupplier;
    this.newVersion = newVersion;
    this.oldVersionUriSupplier = oldValueUriSupplier;
    this.newVersionUri = newVersionUri;
    this.type = type;
  }

  public final Object getOldVersion() {
    if (oldVersion == null) {
      oldVersion = oldVersionSupplier.get();
    }
    return oldVersion;
  }

  public final URI getOldVersionUri() {
    if (oldVersionUri == null) {
      oldVersionUri = oldVersionUriSupplier.get();
    }
    return oldVersionUri;
  }

  public final URI getNewVersionUri() {
    return newVersionUri;
  }

  public final Object getNewVersion() {
    return newVersion;
  }

  public final ObjectChange getChange() {
    if (change == null) {
      Object myOldVersion = getOldVersion();
      Object myNewVersion = getNewVersion();
      ApiObjectRef ref = new ApiObjectRef(null, myOldVersion, new HashMap<>(), "storageSave");
      ref.renderAndCleanChanges();
      ref.mergeObject(myNewVersion);
      change = ref.renderAndCleanChanges().orElse(ObjectChange.EMPTY);
    }
    return change;
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }

  /**
   * Creates a {@link StorageSaveEvent} from a {@link StorageSaveEventObject}
   */
  public static StorageSaveEvent fromObject(StorageSaveEventObject saveEventObject,
      Storage storage) {
    StorageSaveEvent newEvent = new StorageSaveEvent();
    newEvent.newVersionUri = saveEventObject.getNewVersion();
    newEvent.oldVersionUri = saveEventObject.getOldVersion();
    newEvent.newVersion = loadObjectFromUri(newEvent.newVersionUri, storage);
    newEvent.oldVersionSupplier = () -> loadObjectFromUri(newEvent.oldVersionUri, storage);
    newEvent.type = newEvent.newVersion.getClass();
    return newEvent;
  }

  private static Object loadObjectFromUri(URI objUri, Storage storage) {
    if (storage.exists(objUri)) {
      return storage.read(objUri);
    }
    return null;
  }

}
